package com.fosun.stargazer.personal.selenium.service;

import com.fosun.stargazer.personal.selenium.dto.entity.Movie;

public interface Neo4jService {
    /**
     * 新增影片
     * @param movie 电影
     * @return 新增影片的id
     */
     Long insertMovie(final Movie movie);

    /**
     * 更新影片对象中涉及的实体的id,防止重复插入
     * @param movie 影片对象
     */
    void updateMovieId(Movie movie);

    /**
     * 利用发射，调用对象的set方法，修改Id值
     * @param obj 待修改的对象
     * @param pObj 数据库中查出来的对象
     */
    void updateId(Object obj,final Object pObj);
}
