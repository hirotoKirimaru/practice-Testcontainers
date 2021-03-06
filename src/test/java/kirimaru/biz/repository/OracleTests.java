package kirimaru.biz.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

//@Disabled
@SpringBootTest
@Testcontainers
public class OracleTests {

  @Container
  private static final OracleContainer oracle = new OracleContainer("oracleinanutshell/oracle-xe-11g:latest");
//      .withUsername("devuser")
//      .withPassword("devuser")
//      .withDatabaseName("devdb"); // MySQLのコンテナを生成

  @Autowired
  SampleMapper mapper;

  @DynamicPropertySource
  static void setup(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", oracle::getJdbcUrl); // コンテナで起動中のMySQLへ接続するためのJDBC URLをプロパティへ設定
  }

  @Test
  void contextLoads() {
    Assertions.assertThat(oracle.isRunning()).isTrue();
//    {
//      MysqlTestDemoApplication.Sample sample = new MysqlTestDemoApplication.Sample();
//      sample.id = 1;
//      sample.name = "Test";
////      mapper.create(sample); // データをMySQLへ追加
////    }
//    {
//      MysqlTestDemoApplication.Sample sample = mapper.findOne(1); // MySQLへ追加したデータを取得
//      Assertions.assertThat(sample.id).isEqualTo(1);
//      Assertions.assertThat(sample.name).isEqualTo("Test");
//    }
  }
}
