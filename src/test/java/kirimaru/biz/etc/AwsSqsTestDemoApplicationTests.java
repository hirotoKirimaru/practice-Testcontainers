package kirimaru.biz.etc;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Testcontainers
class AwsSqsTestDemoApplicationTests {

  @Container
  static final LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack"))
      .withServices(LocalStackContainer.Service.SQS); // Mock SQSを有効にした状態でLocalStackコンテナ(Emulator)を作成

  @Autowired
  AmazonSQSAsync amazonSQSAsync;

  @Autowired
  BlockingQueue<Message<String>> messages;

  @DynamicPropertySource
  static void setup(DynamicPropertyRegistry registry) {
    AmazonSQS amazonSQS = AmazonSQSClientBuilder.standard()
        .withEndpointConfiguration(localstack.getEndpointConfiguration(LocalStackContainer.Service.SQS))
        .withCredentials(localstack.getDefaultCredentialsProvider())
        .build();
    amazonSQS.createQueue("demoQueue"); // Queueの作成
    // ↓ コンテナで起動中のSQS Emulatorへ接続するための資格情報をプロパティへ設定
    registry.add("cloud.aws.credentials.access-key", localstack::getAccessKey);
    registry.add("cloud.aws.credentials.secret-key", localstack::getSecretKey);
    // ↓ コンテナで起動中のSQS Emulatorへ接続するためのリージョン情報をプロパティへ設定
    registry.add("cloud.aws.region.static", localstack::getRegion);
    // ↓ コンテナで起動中のSQS Emulatorへ接続するための接続情報をプロパティへ設定
    registry.add("cloud.aws.sqs.endpoint", localstack.getEndpointConfiguration(LocalStackContainer.Service.SQS)::getServiceEndpoint);
  }

  @Test
  void contextLoads() throws InterruptedException {
    QueueMessagingTemplate template = new QueueMessagingTemplate(amazonSQSAsync);
    template.send("demoQueue", MessageBuilder.withPayload("Hello World!").build()); // SQS Emulatorへメッセージを送信

    Message<String> message = messages.poll(10, TimeUnit.SECONDS); // SQS Emulatorから受信したメッセージを取得
    Assertions.assertThat(message.getPayload()).isEqualTo("Hello World!");
  }

}
