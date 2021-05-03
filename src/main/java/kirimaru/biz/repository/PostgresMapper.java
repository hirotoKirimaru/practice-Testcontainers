package kirimaru.biz.repository;

import kirimaru.biz.domain.Sample;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PostgresMapper {
  @Insert("INSERT INTO SAMPLE (id, name) values(#{domain.id}, #{domain.name})")
  void insert(@Param("domain") Sample sample);

  @Select("SELECT * FROM SAMPLE WHERE id = #{id}")
  Sample findById(@Param("id") int id);
}
