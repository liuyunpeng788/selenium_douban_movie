package com.fosun.stargazer.personal.selenium.util;

import com.fosun.stargazer.personal.selenium.dto.entity.Movie;
import com.fosun.stargazer.personal.selenium.dto.entity.MovieType;
import com.fosun.stargazer.personal.selenium.dto.entity.ReleasePlace;
import com.fosun.stargazer.personal.selenium.dto.relationship.MovieTypeShip;
import com.fosun.stargazer.personal.selenium.dto.relationship.ReleasePlaceShip;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
    private static Logger logger = LoggerFactory.getLogger("CommonUtil");

    /**
     * 获取电影类型信息
     * @param typeInfo 影片类型
     * @param movie 电影对象
     */
    public static void buildMovieTypeShip(String typeInfo, final Movie movie, final WebElement element){
        Set<MovieTypeShip> movieTypeShips = new HashSet<>();
        for(String movieTypeInfo : typeInfo.split("/")){
            if(StringUtils.isBlank(movieTypeInfo)){ continue;}
            MovieType movieType = new MovieType();
            MovieTypeShip movieTypeShip = new MovieTypeShip(movie,movieType);
            movieType.setName(movieTypeInfo.toString());
            String betterThan = element.findElement(By.xpath("//div[@class='rating_betterthan']")).findElement(By.cssSelector("a[href*='"+ movieTypeInfo.trim()+"']")).getText();
            if(StringUtils.isNotBlank(betterThan)){
                Pattern betterPattern = Pattern.compile("\\D+([0-9.%]+)\\D+");
                Matcher matcher = betterPattern.matcher(betterThan);
                if(matcher.find()){
                    movieTypeShip.setBetterProportion(matcher.group(1).trim());
                }
                movieTypeShips.add(movieTypeShip);
            }
            movie.setMovieTypeShips(movieTypeShips);
        }
    }

    /**
     * 获取电影类型信息
     * @param typeInfo 影片类型
     * @param movie 电影对象
     */
    @Deprecated
    public static void buildMovieTypeShip(String typeInfo, final Movie movie){
        Set<MovieTypeShip> movieTypeShips = new HashSet<>(2);
        for(String type: typeInfo.split("/")){
            MovieType movieType = new MovieType();
            movieType.setName(type.trim());
            MovieTypeShip typeShip = new MovieTypeShip();
            typeShip.setMovie(movie);
            typeShip.setMovieType(movieType);
            movieTypeShips.add(typeShip);
        }
        movie.setMovieTypeShips(movieTypeShips);
    }

    //提取电影名称和发行年份信息
    public static String[] regexMovieNameAndYearInfo(String movieName){
        String[] movieNameAndInfo = new String[2];
        String pattern = "(\\D*)\\((\\d{4})*";
        Integer year = null;
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(movieName);
        if(m.find()) {
            movieNameAndInfo[0]= m.group(1);
            movieNameAndInfo[1] = m.group(2);
            logger.info("match success. movieName:" + m.group(1) + ", year:" + m.group(2));
        }else{
            logger.info("match movieName and year failure...");
        }
        return movieNameAndInfo;
    }
    /**
     * 处理首映关系
     * @param releasePlaceInfo 首映信息字符串
     * @param movie 电影
     */
    public static void buildReleasePlaceShip(String releasePlaceInfo, Movie movie) {
        Set<ReleasePlaceShip> releasePlaceShips = new HashSet<>(2);
        for(String releaseInfo: releasePlaceInfo.split("/")){
            ReleasePlaceShip releasePlaceShip = new ReleasePlaceShip();
            ReleasePlace releasePlace = new ReleasePlace();
            releaseInfo = releaseInfo.replace("（","(").replace("）",")");
            int index = releaseInfo.indexOf("(");
            releasePlace.setName(releaseInfo.substring(index + 1,releaseInfo.lastIndexOf(")")));
            releasePlaceShip.setMovie(movie);
            releasePlaceShip.setReleasePlace(releasePlace);
            releasePlaceShip.setTime(releaseInfo.substring(1,index));
            releasePlaceShips.add(releasePlaceShip);
        }
        movie.setReleasePlaceShips(releasePlaceShips);

    }


    /**
     * 创建文件
     * @param filename 文件名
     * @return
     * @throws Exception
     */
    public static File createFile(String filename) throws Exception{
        File file = new File(filename);
        if(!file.getParentFile().exists()){
            file.getParentFile().createNewFile();
            file.createNewFile();
        }
        return file;
    }
}
