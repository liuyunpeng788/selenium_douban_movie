package com.fosun.stargazer.personal.selenium.repository.neo4j;

import com.fosun.stargazer.personal.selenium.dto.entity.Movie;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//@RepositoryRestResource(collectionResourceRel = "movies", path = "movies")
@Repository
public interface MovieRepository extends Neo4jRepository<Movie, Long> {
    // returns the node with id equal to idOfMovie parameter
    @Query("MATCH (n:Movie) WHERE id(n)={0} RETURN n")
    Movie getMovieById(Long id);

    @Query("MATCH (n:Movie) WHERE n.name={0} and n.year = {1} RETURN n limit 1")
    Movie findByNameAndYear(String name, Integer year);

    /**
     * 这是个错误的写法
     * 新增一个电影记录，如果重复的话，则会更新
     * @param movie 电影对象
     * @return 新增的电影对象的id
     */
//    @Query("MERGE(m:Movie) " +
//            "ON CREATE SET m.name =#movie.name,m.alias=#movie.alias, m.year=#movie.year, m.area=#movie.area,m.duration=#movie.duration, m.summary=#movie.summary,m.score=#movie.score,m.commentNumber=#movie.commentNumber,m.scoreDetail=#movie.scoreDetail,m.category=#movie.category" +
//            "ON MATCH SET m.name =#movie.name,m.alias=#movie.alias, m.year=#movie.year, m.area=#movie.area,m.duration=#movie.duration, m.summary=#movie.summary,m.score=#movie.score,m.commentNumber=#movie.commentNumber,m.scoreDetail=#movie.scoreDetail,m.category=#movie.category"+
//            "RETURN id(m)"
//    )
//    Long saveMovie(@Param("movie") Movie movie);



}
