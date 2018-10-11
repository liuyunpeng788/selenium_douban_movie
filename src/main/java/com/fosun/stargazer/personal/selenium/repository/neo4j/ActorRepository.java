package com.fosun.stargazer.personal.selenium.repository.neo4j;

import com.fosun.stargazer.personal.selenium.dto.entity.Actor;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends CrudRepository<Actor,Long> {

    @Query("Match(p:Actor{ chName : {0},engName:{1}}) return p limit 1")
    Actor queryActorsByNameAndAlias(String chName,String engName);

}
