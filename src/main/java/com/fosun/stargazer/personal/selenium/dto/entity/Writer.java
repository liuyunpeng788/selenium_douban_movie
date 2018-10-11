package com.fosun.stargazer.personal.selenium.dto.entity;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

/**
 * 编剧
 */
@NodeEntity(label = "Writer")
public class Writer {

    @Id
    @GeneratedValue
    private Long id;

    @Property
    private String chName;  //中文名
    @Property
    private String engName; //英文名

    @Property
    private String representativeWork;  //代表作
    @Property
    private String born; //出生日期

    public String getRepresentativeWork() {
        return representativeWork;
    }

    public void setRepresentativeWork(String representativeWork) {
        this.representativeWork = representativeWork;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBorn() {
        return born;
    }

    public void setBorn(String born) {
        this.born = born;
    }

    public Writer() {
    }
}
