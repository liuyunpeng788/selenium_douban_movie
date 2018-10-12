package com.fosun.stargazer.personal.selenium.thread;

import com.fosun.stargazer.personal.selenium.service.SeleniumService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @deprecated
 * 多线程处理：暂时没有什么用
 */
public class MovieDetailTask implements Callable<List<String>>{

    @Autowired
    private SeleniumService seleniumService;

    private List<String> urlList;


    public List<String> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }

    public MovieDetailTask(List<String> urlList) {
        this.urlList = urlList;
    }

    @Override
    public List<String> call() throws Exception {
        seleniumService.getMovieDetailInfo("");
        return null;
    }
}
