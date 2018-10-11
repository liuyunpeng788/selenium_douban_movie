package com.fosun.stargazer.personal.selenium.service;

import com.fosun.stargazer.personal.selenium.dto.entity.Movie;

import javax.annotation.Nullable;

public interface SeleniumService {
    @Nullable
    Movie getMovieDetailInfo(String url);
}
