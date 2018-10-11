package com.fosun.stargazer.personal.selenium.service;

import com.fosun.stargazer.personal.selenium.dto.entity.Movie;

import javax.annotation.Nullable;
import java.util.Set;

public interface SeleniumService {
    @Nullable
    Movie getMovieDetailInfo(String url);

    /**
     * 获取电影链接列表
     * @param originUrl 起始url
     * @return 电影链接list
     */
    Set<String> getMovieLinks(String originUrl);
}
