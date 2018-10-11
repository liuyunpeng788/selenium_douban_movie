package com.fosun.stargazer.personal.selenium.repository.neo4j;

import com.fosun.stargazer.personal.selenium.dto.entity.Director;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DirectorRepository extends CrudRepository<Director,Long> {
    @Query("Match(p:Director{ chName : {0},engName:{1}}) return p limit 1")
    Director queryDirectorByNameAndAlias(String chName, String engName);
}
