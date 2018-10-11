package com.fosun.stargazer.personal.selenium.dto.relationship;

import com.alibaba.fastjson.JSONObject;
import com.fosun.stargazer.personal.selenium.dto.entity.Movie;
import com.fosun.stargazer.personal.selenium.dto.entity.ReleasePlace;
import org.neo4j.ogm.annotation.*;

/**
 * 首映关系
 * 包含一个时间属性
 */
@RelationshipEntity(type="FIRST_RELEASED_IN")
public class ReleasePlaceShip {
    @Id
    @GeneratedValue
    private Long id;  //关系的id
    @Property
    private String time; //首映时间
    private String title;
    @StartNode
    private ReleasePlace releasePlace;
    @EndNode
    private Movie movie;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ReleasePlace getReleasePlace() {
        return releasePlace;
    }

    public void setReleasePlace(ReleasePlace releasePlace) {
        this.releasePlace = releasePlace;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public ReleasePlaceShip() {
    }
}
