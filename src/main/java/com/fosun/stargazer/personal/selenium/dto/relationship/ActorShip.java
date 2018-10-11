package com.fosun.stargazer.personal.selenium.dto.relationship;

import com.alibaba.fastjson.JSONObject;
import com.fosun.stargazer.personal.selenium.dto.entity.Actor;
import com.fosun.stargazer.personal.selenium.dto.entity.Movie;
import org.neo4j.ogm.annotation.*;

@RelationshipEntity(type = "ROLE_OF")
public class ActorShip {
    @Id
    @GeneratedValue
    private Long id;  //关系的id

    @Property
    private String roleName;  //角色名称

    @StartNode
    private Actor actor;

   @EndNode
    private Movie movie;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Actor getActor() {
        return actor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public ActorShip() {
    }

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }
}
