package com.fosun.stargazer.personal.selenium.repository.neo4j;

import com.fosun.stargazer.personal.selenium.dto.entity.Producer;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProducerRepository extends CrudRepository<Producer,Long> {
    @Query("Match(p:Producer{ chName:{0},engName:{1}}) return p limit 1")
    Producer queryProducerByName(String chName,String engName);
}
