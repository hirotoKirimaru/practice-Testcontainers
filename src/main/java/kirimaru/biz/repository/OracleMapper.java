package kirimaru.biz.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OracleMapper {
  @Insert("INSERT INTO SAMPLE (id, name) values('1', '2')")
  void insert();
}
