package kirimaru.biz.etc;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Testcontainers
@ActiveProfiles("kafka")
class KafkaTestDemoApplicationTests {

  @Container
  static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka")); // Kafkaのコンテナを生成

  @Autowired
  KafkaOperations<?, ?> kafkaOperations;

  @Autowired
  BlockingQueue<Message<String>> messages;

  @DynamicPropertySource
  static void setup(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers); // コンテナで起動中のKafkaへ接続するための接続情報をプロパティへ設定
  }

  @Test
  void contextLoads() throws InterruptedException {
    Assertions.assertThat(kafka.isRunning()).isTrue();
    kafkaOperations.send(MessageBuilder.withPayload("Hello!").build()); // Kafkaへメッセージを送信

    Message<String> message = messages.poll(10, TimeUnit.SECONDS); // Kafkaから受信したメッセージを取得
    Assertions.assertThat(message.getPayload()).isEqualTo("Hello!");
  }

}
