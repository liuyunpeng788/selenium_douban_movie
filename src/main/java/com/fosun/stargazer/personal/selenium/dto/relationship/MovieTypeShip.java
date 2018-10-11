package com.fosun.stargazer.personal.selenium.dto.relationship;

import com.alibaba.fastjson.JSONObject;
import com.fosun.stargazer.personal.selenium.dto.entity.Movie;
import com.fosun.stargazer.personal.selenium.dto.entity.MovieType;
import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "TYPE_OF")
public class MovieTypeShip {
    @Id
    @GeneratedValue
    private Long id;  //关系的id

    @Property
    private String betterProportion;  //好看的占比

    @StartNode
    private Movie movie;

    @EndNode
    private MovieType movieType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBetterProportion() {
        return betterProportion;
    }

    public void setBetterProportion(String betterProportion) {
        this.betterProportion = betterProportion;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public MovieType getMovieType() {
        return movieType;
    }

    public void setMovieType(MovieType movieType) {
        this.movieType = movieType;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public MovieTypeShip() {
    }

    public MovieTypeShip(Movie movie, MovieType movieType) {
        this.movie = movie;
        this.movieType = movieType;
    }
}
