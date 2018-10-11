package com.fosun.stargazer.personal.selenium.repository.neo4j;

import com.fosun.stargazer.personal.selenium.dto.entity.Writer;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WriterRepository extends CrudRepository<Writer,Long> {

    @Query("Match(p:Writer{ chName:{0},engName :{1}}) return p limit 1")
    Writer queryWritersByNameAndAlias(String chName, String engName);

}
