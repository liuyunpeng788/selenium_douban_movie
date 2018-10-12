package com.fosun.stargazer.personal.selenium.service;

import com.fosun.stargazer.personal.selenium.dto.entity.Movie;

import javax.annotation.Nullable;
import java.util.Set;

public interface SeleniumService {
    /**
     * 获取指定页的电影链接地址
     * @param originUrl 起始爬虫地址
     * @param startNo 起始页号
     * @param pageSize 页面电影数
     */
    void getMovies(String originUrl,Integer startNo,Integer pageSize);

    /**
     * 获取电影详情
     * @param url 电影链接地址
     * @return 电影
     */
    Movie getMovieDetailInfo(String url);
}
