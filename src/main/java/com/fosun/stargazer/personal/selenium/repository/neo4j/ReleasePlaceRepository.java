package com.fosun.stargazer.personal.selenium.repository.neo4j;

import com.fosun.stargazer.personal.selenium.dto.entity.ReleasePlace;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReleasePlaceRepository extends CrudRepository<ReleasePlace,Long>{
    @Query("Match(p:ReleasePlace{name:{0}}) return p limit 1")
    ReleasePlace queryReleasePlaceByName(String name);
}
