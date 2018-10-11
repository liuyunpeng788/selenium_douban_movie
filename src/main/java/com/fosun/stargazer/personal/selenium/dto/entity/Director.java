package com.fosun.stargazer.personal.selenium.dto.entity;

import com.alibaba.fastjson.JSONObject;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label = "Director")
public class Director {
    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String chName;  //中文名

    @Property
    private String engName; //英文名

    @Property
    private String born; //出生日期

    @Property
    private String representativeWork;  //代表作


    public String getRepresentativeWork() {
        return representativeWork;
    }

    public void setRepresentativeWork(String representativeWork) {
        this.representativeWork = representativeWork;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public String getEngName() {
        return engName;
    }

    public void setEngName(String engName) {
        this.engName = engName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBorn() {
        return born;
    }

    public void setBorn(String born) {
        this.born = born;
    }

    public Director() {
    }

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

}
