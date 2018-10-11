package com.fosun.stargazer.personal.selenium.dto.entity;

import com.alibaba.fastjson.JSONObject;

public class Person {
    private String name;  //姓名
    private String alias; //别名
    private Integer age;
    private String gender;   //性别
    private String citizenship;  //国籍
    private String nationality;  //民族
    private String marriage;   //婚姻状况

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    @Override
    public String toString(){
        return JSONObject.toJSONString(this);
    }

    public Person() {
    }
}
