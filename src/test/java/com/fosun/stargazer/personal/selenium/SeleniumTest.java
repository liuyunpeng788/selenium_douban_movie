package com.fosun.stargazer.personal.selenium;

import com.fosun.stargazer.personal.selenium.dto.entity.Movie;
import com.fosun.stargazer.personal.selenium.dto.entity.ReleasePlace;
import com.fosun.stargazer.personal.selenium.dto.relationship.ActorShip;
import com.fosun.stargazer.personal.selenium.dto.relationship.MovieTypeShip;
import com.fosun.stargazer.personal.selenium.dto.relationship.ReleasePlaceShip;
import com.fosun.stargazer.personal.selenium.repository.neo4j.*;
import com.fosun.stargazer.personal.selenium.service.Neo4jService;
import com.fosun.stargazer.personal.selenium.service.SeleniumService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SeleniumTest {
    @Autowired
    private SeleniumService seleniumService;

    @Autowired
    private Neo4jService neo4jService;

    @Test
    public void seleniumData(){
        try{
            Movie movie = seleniumService.getMovieDetailInfo("https://movie.douban.com/subject/26985127/?tag=%E7%83%AD%E9%97%A8&from=gaia_video");
            if(null == movie){
                System.out.println("movie is null");
                return ;
            }
            Long res = neo4jService.insertMovie(movie);
            System.out.println("res :" +  res);
        }catch (Exception ex){
            ex.printStackTrace();
        }


    }

}
