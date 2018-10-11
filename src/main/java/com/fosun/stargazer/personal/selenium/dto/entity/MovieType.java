package com.fosun.stargazer.personal.selenium.dto.entity;

import com.alibaba.fastjson.JSONObject;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label = "MovieType")
public class MovieType {
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String name;

//    @Relationship(type="TYPE_OF" )
//    private Movie Movie;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

    public MovieType() {
    }


}
