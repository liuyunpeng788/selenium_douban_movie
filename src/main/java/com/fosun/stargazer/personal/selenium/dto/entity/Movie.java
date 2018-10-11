package com.fosun.stargazer.personal.selenium.dto.entity;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fosun.stargazer.personal.selenium.dto.relationship.ActorShip;
import com.fosun.stargazer.personal.selenium.dto.relationship.MovieTypeShip;
import com.fosun.stargazer.personal.selenium.dto.relationship.ReleasePlaceShip;
import org.neo4j.ogm.annotation.*;

import java.util.Set;

@NodeEntity(label = "Movie")
public class Movie {
    @Id
    @GeneratedValue
    private Long id;
    @Property
    private String name; //电影名称
    @Property
    private String alias; //别名
    @Property
    private Integer year;       //所属年份
    @Property
    private String area;         //国家/地区
    @Property
    private String duration;      //时长
    @Property
    private String summary;         //剧情简介
    @Property
    private Double score;         //评分
    @Property
    private Integer commentNumber;    //评价的人数
    @Property
    private String scoreDetail;         //评分详情 一星到五星评价的比例详情 jsonstring star1:55%
    @Property
    private String category;         //类别，如电影，电视剧等

    @Property
    private String language;

    @Relationship(type="WRITE",direction = Relationship.INCOMING)
    private Set<Writer> writers;         //编剧 ，抽象为一个实体

    @Relationship(type="DIRECT",direction = Relationship.INCOMING)
    private Director director;       //导演

    @Relationship(type="Producer",direction = Relationship.INCOMING)
    private Set<Producer> producers; //制片人

    @JsonIgnoreProperties("movie")
    @Relationship(type="FIRST_RELEASED_IN" )
    private Set<ReleasePlaceShip> releasePlaceShips;  //上映日期: 2018-03-09(西南偏南电影节) / 2018-07-20(美国) ,  抽象为实体

    @JsonIgnoreProperties("movie")
    @Relationship(type="TYPE_OF")
    private Set<MovieTypeShip> MovieTypeShips;  //影片类型（多个类型要分开） ,抽象为实体，通过关系：ofType{score} ,score表示评价占比高于同类型的数值

    @JsonIgnoreProperties("movie")
    @Relationship(type="ROLE_OF" ,direction = Relationship.INCOMING)
    private Set<ActorShip> actorShips;    //演员-电影关系

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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(Integer commentNumber) {
        this.commentNumber = commentNumber;
    }

    public String getScoreDetail() {
        return scoreDetail;
    }

    public void setScoreDetail(String scoreDetail) {
        this.scoreDetail = scoreDetail;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Set<Writer> getWriters() {
        return writers;
    }

    public void setWriters(Set<Writer> writers) {
        this.writers = writers;
    }

    public Set<ActorShip> getActorShips() {
        return actorShips;
    }

    public void setActorShips(Set<ActorShip> actorShips) {
        this.actorShips = actorShips;
    }

    public Set<Producer> getProducers() {
        return producers;
    }

    public void setProducers(Set<Producer> producers) {
        this.producers = producers;
    }

    public Set<ReleasePlaceShip> getReleasePlaceShips() {
        return releasePlaceShips;
    }

    public void setReleasePlaceShips(Set<ReleasePlaceShip> releasePlaceShips) {
        this.releasePlaceShips = releasePlaceShips;
    }

    public Set<MovieTypeShip> getMovieTypeShips() {
        return MovieTypeShips;
    }

    public void setMovieTypeShips(Set<MovieTypeShip> movieTypeShips) {
        MovieTypeShips = movieTypeShips;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }



    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public Director getDirector() {
        return director;
    }

    public void setDirector(Director director) {
        this.director = director;
    }

    public Movie() {
    }


}
