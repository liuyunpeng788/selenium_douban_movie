package com.fosun.stargazer.personal.selenium.repository.neo4j;

import com.fosun.stargazer.personal.selenium.dto.relationship.MovieTypeShip;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieTypeShipRepository extends CrudRepository<MovieTypeShip,Long> {

    @Query("Match(m:Movie)-[r:TYPE_OF]->(t:MovieType) WHERE m.name = {0} and m.year={1} and t.name={2} return r limit 1")
    MovieTypeShip getMovieTypeShipByMovieAndMovieType(String movieName,Integer year ,String movieTypeName);

}
