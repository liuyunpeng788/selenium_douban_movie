package com.fosun.stargazer.personal.selenium.repository.neo4j;

import com.fosun.stargazer.personal.selenium.dto.entity.MovieType;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieTypeRepository extends CrudRepository<MovieType,Long> {

        @Query("Match(p:MovieType{name : {0}}) return p limit 1")
       MovieType queryMovieTypeByName(String name);
}
