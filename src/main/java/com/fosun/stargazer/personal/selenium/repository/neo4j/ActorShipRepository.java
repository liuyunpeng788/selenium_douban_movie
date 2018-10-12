package com.fosun.stargazer.personal.selenium.repository.neo4j;

import com.fosun.stargazer.personal.selenium.dto.relationship.ActorShip;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorShipRepository extends CrudRepository<ActorShip,Long> {
    @Query("Match(p:Actor)-[r:ROLE_OF]->(m:Movie) where p.chName = {0} and m.name={1} and m.year={2} return r limit 1")
    ActorShip getActorShipByActorAndMovie(String actorName,String movieName,Integer year);
}
