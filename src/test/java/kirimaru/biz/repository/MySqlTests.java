package kirimaru.biz.repository;

import kirimaru.biz.domain.Sample;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Disabled("INSERT文が違う…？")
@SpringBootTest
@Testcontainers
@ActiveProfiles("mysql")
public class MySqlTests {

  @Container
  private static final MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql"))
      .withUsername("devuser")
      .withPassword("devuser")
      .withDatabaseName("devdb"); // PostgreSQLのコンテナを生成

  @Autowired
  SampleMapper mapper;

  @DynamicPropertySource
  static void setup(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", mysql::getJdbcUrl); // コンテナで起動中のMySQLへ接続するためのJDBC URLをプロパティへ設定
  }

  @Test
  void contextLoads() {
    Assertions.assertThat(mysql.isRunning()).isTrue();
    {
      Sample sample = Sample.builder()
          .id(1)
          .name("Test")
          .build();
      mapper.insert(sample); // データをMySQLへ追加
    }
    {
      Sample sample = mapper.findById(1); // MySQLへ追加したデータを取得
      Assertions.assertThat(sample.getId()).isEqualTo(1);
      Assertions.assertThat(sample.getName()).isEqualTo("Test");
    }
  }
}
