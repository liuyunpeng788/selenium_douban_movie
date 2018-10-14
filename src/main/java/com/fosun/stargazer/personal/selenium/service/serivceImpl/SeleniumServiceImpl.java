package com.fosun.stargazer.personal.selenium.service.serivceImpl;

import com.fosun.stargazer.personal.selenium.dto.entity.Actor;
import com.fosun.stargazer.personal.selenium.dto.entity.Director;
import com.fosun.stargazer.personal.selenium.dto.entity.Movie;
import com.fosun.stargazer.personal.selenium.dto.entity.Writer;
import com.fosun.stargazer.personal.selenium.dto.relationship.ActorShip;
import com.fosun.stargazer.personal.selenium.service.Neo4jService;
import com.fosun.stargazer.personal.selenium.service.SeleniumService;
import com.fosun.stargazer.personal.selenium.thread.MovieDetailTask;
import com.fosun.stargazer.personal.selenium.util.CommonUtil;
import com.fosun.stargazer.personal.selenium.util.SeleniumUtil;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("SeleniumService")
public class SeleniumServiceImpl implements SeleniumService {
    private static Logger logger = LoggerFactory.getLogger("SeleniumServiceImpl");

    @Autowired
    private Neo4jService neo4jService;

    /**
     * 获取指定页的电影链接地址
     * @param originUrl 起始爬虫地址
     * @param startNo 起始页号
     * @param pageSize 页面电影数
     */
    @Override
    public void getMovies(String originUrl,Integer startNo,Integer pageSize) {
        startNo = null == startNo? 1:startNo;  //起始电影编号
        pageSize = null == pageSize? 20:pageSize;
        logger.info("startNo:" + startNo +",pageSize:" + pageSize);
        WebDriver webDriver = new ChromeDriver();


        try{
            webDriver.get(originUrl);
            webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            for(int i = 1;i<startNo%pageSize && startNo > pageSize;i++){
                if (!getMore(webDriver,startNo,pageSize)){
                    logger.error("获取更多影片信息失败，直接退出");
                    webDriver.close();
                    return;
                }else{
                    startNo += pageSize;
                }
            }

            String preHrefIndex = "//div[@class='list-wp']//div[@class='list']//a[@class='item']";
            String hrefIndex = preHrefIndex + "["+ startNo +"]";
            WebElement webElement = SeleniumUtil.getWebElementByWebDriver(webDriver,By.xpath(hrefIndex));  //如果找不到会直接抛异常
            while(null != webElement){
                String url = SeleniumUtil.getWebElementAttributeValue(webElement,"href");
                //处理电影
                Movie movie = getMovieDetailInfo(url);
                if(null != movie){
                    neo4jService.insertMovie(movie);
                }
                startNo++;
                logger.info("startNo:" + startNo + ", url:" + url );

                if(startNo%(pageSize+1)== 0){
                    if( !getMore(webDriver,startNo,pageSize)){
                        logger.error("获取更多影片信息失败，结束此次循环");
                        break;
                    }
                }
                hrefIndex = preHrefIndex + "["+ startNo +"]";
                webElement = SeleniumUtil.getWebElementByWebDriver( webDriver,By.xpath(hrefIndex));
            }
        }catch (Exception ex){
            logger.error(ExceptionUtils.getStackTrace(ex));
            CommonUtil.saveScreenshot(webDriver);
        }finally {
            webDriver.close();
        }

    }


    /**
     * 点击获取更多
     */
    private boolean getMore(WebDriver webDriver,Integer startNo,Integer pageSize ){
        startNo = null == startNo? 1:startNo;  //起始电影编号
        pageSize = null == pageSize? 20:pageSize;
        boolean res = true;
        //点击获取更多
       WebElement webElement = SeleniumUtil.getWebElementByWebDriver(webDriver,By.xpath("//a[@class='more']"));
        if(null  != webElement){
            webElement.click();
        }else{
            logger.error("当前page点击获取更多失败,pageNo:"+ (startNo%pageSize + 1));
            res = false;
        }
        return res;
    }

    //多线程计算 ，其实没有啥用
    @Deprecated
    private void multiThreadGetUrlLinks(final Set<String> urlLinks){
        if(null != urlLinks){
            List<String> list = new ArrayList<>(urlLinks);
            int threadNum = (int)Math.ceil(urlLinks.size()/10);
            ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threadNum));
            for(int i = 0;i< threadNum; i++){
                int end = 10*(i+1) > urlLinks.size()? urlLinks.size():10*(i+1);
                executorService.submit(new MovieDetailTask(list.subList(i*10,end)));
            }
        }
    }

    @Override
    public Movie getMovieDetailInfo(String url) {
        if(StringUtils.isBlank(url)){
            logger.error("url is null");
            return null;
        }
        logger.info("get movie detail info ,url:" + url);
        Movie movie = new Movie();
        // 创建了一个 chrome driver 的实例
        // 注意，其余的代码依赖于接口而非实例
        WebDriver webDriver = new ChromeDriver();
        try{
            //访问站点 或者使用 webDriver.navigate().to(url);
            webDriver.get(url);
            WebElement element = SeleniumUtil.getWebElementByWebDriver(webDriver,By.xpath("//div[@id='content']"),By.tagName("h1"));
            if(null == element){
                logger.error( "获取电影信息失败，url:" + url);
                return null;
            }
            //获取电影名
            String movieName = element.getText();
            logger.info("movie name :" + movieName);

            String[] arr = CommonUtil.regexMovieNameAndYearInfo(movieName);
            movie.setName(arr[0]); //电影名
            if(StringUtils.isNotBlank(arr[1])){ movie.setYear(Integer.valueOf(arr[1]));  }//电影的发行年份

            String infos = SeleniumUtil.getWebElementText(element,By.xpath("//div[@id='info']"));
            if(StringUtils.isNotBlank(infos)){
                for(String line : infos.split("\n")){
                    if(StringUtils.isNotBlank(line)){
                        String[] info = line.split(":");
                        if(StringUtils.isBlank(info[1])){ continue;}  //如果属性值为空，则继续
                        if(Objects.equals("类型",info[0].trim())){
                            //处理影片类型信息
                            CommonUtil.buildMovieTypeShip(info[1],movie,element);
                        }else if(Objects.equals("制片国家/地区",info[0].trim())){
                            movie.setArea(info[1].trim());
                        }else if(Objects.equals("语言",info[0].trim())){
                            movie.setLanguage(info[1].trim());
                        }else if(Objects.equals("上映日期",info[0].trim())){
                            CommonUtil.buildReleasePlaceShip(info[1],movie); //处理上映信息
                        }else if(Objects.equals("片长",info[0].trim())){
                            movie.setDuration(info[1]);
                        }else if(Objects.equals("又名",info[0].trim()) ){
                            movie.setAlias(info[1].trim());
                        }
                    }
                }
            }

            String scoreDetail = SeleniumUtil.getWebElementText (element,By.xpath("//div[@class='ratings-on-weight']"));
            Pattern newPatten = Pattern.compile("(\\d+星)\\D+(\\d+[.]?\\d+%)");
            Matcher newMatcher = newPatten.matcher(scoreDetail);
            String detail = "";
            while(newMatcher.find()){
                detail += newMatcher.group(1) + ":" + newMatcher.group(2)+",";
            }
            if(StringUtils.isNotBlank(detail) && detail.length() >1){
                movie.setScoreDetail(detail.substring(0,detail.length()-1));  //评分详情
            }

            String score = SeleniumUtil.getWebElementText (element,By.xpath("//div[@class='rating_self clearfix']//strong[@class='ll rating_num']"));
            String num = SeleniumUtil.getWebElementText (element,By.xpath("//div[@class='rating_sum']//span[@property='v:votes']")); //评价的人数
            movie.setScore(StringUtils.isNotBlank(score)? Double.valueOf(score):0.0);
            movie.setCommentNumber(StringUtils.isNotBlank(num)? Integer.valueOf(num):0);


            //查看演员信息
            element = element.findElement(By.xpath("//div[@id='celebrities']//span[@class='pl']"));
            element.click();
            List<WebElement> elementList = webDriver.findElements(By.xpath("//div[@id ='celebrities']//div[@class='list-wrapper']"));

            if(null != elementList){
                elementList.parallelStream().forEach(x->{
                    String roleName =SeleniumUtil.getWebElementText(x,By.tagName("h2"));
                    if(roleName.contains("导演")){
                        String name = SeleniumUtil.getWebElementText(x,By.cssSelector(".info .name")); //人名
                        String works = SeleniumUtil.getWebElementText(x,By.cssSelector(".info .works")); //代表作

                        Director director = new Director();
                        director.setChName(name.split(" ",2)[0]); // limit 表示分隔成几部分
                        director.setEngName(name.split(" ",2)[1]);
                        if(StringUtils.isNotBlank(works)){ director.setRepresentativeWork(works.replace("：",":").split(":")[1].trim().replace(" ","/")); }//代表作
                        logger.info(director.toString());
                        movie.setDirector(director);

                    }else if(roleName.contains("演员")){
                        Set<ActorShip> actorShips =  new HashSet<>();
                        List<WebElement> actorList =  x.findElements(By.cssSelector(".celebrities-list  >.celebrity "));
                        if(null != actorList){
                            actorList.parallelStream().forEach(actorInfo->{
                                String name = SeleniumUtil.getWebElementText(actorInfo,By.cssSelector(".info .name"));//人名
                                String role = SeleniumUtil.getWebElementText(actorInfo,By.cssSelector(".info .role")); //角色
                                String works =SeleniumUtil.getWebElementText(actorInfo,By.cssSelector(".info .works")); //代表作
                                if(StringUtils.isNotBlank(role) && role.contains("饰")){ //如果有角色
                                     role = role.replaceAll("[()（）]","").trim();
                                 }

                                Actor actor = new Actor();
                                actor.setChName( name.split(" ",2)[0]);
                                actor.setEngName(name.split(" ",2)[1]);
                                logger.info(actor.toString());
                                if(StringUtils.isNotBlank(works)){ actor.setRepresentativeWork(works.replace("：",":").split(":")[1].trim().replace(" ","/")); }

                                ActorShip actorShip = new ActorShip();
                                actorShip.setActor(actor);
                                actorShip.setMovie(movie);
                                actorShip.setRoleName(role); //扮演的角色
                                logger.info(actorShip.toString());
                                actorShips.add(actorShip);
                            });
                        }
                        movie.setActorShips(actorShips);  //设置演员关系

                    }else if(roleName.contains("编剧")){
                        Set<Writer> writers = new HashSet<>();
                        List<WebElement> writerList = x.findElements(By.cssSelector(".celebrities-list  >.celebrity "));

                        if(null != writerList){
                            writerList.parallelStream().forEach(writerInfo->{
                                String name = SeleniumUtil.getWebElementText(writerInfo,By.cssSelector(".info >.name")); //人名
                                String works = SeleniumUtil.getWebElementText(writerInfo,By.cssSelector(".info >.works")); //代表作
                                Writer writer = new Writer();
                                writer.setChName(name.split(" ",2)[0]);
                                writer.setEngName(name.split(" ",2)[1]);
                                if(StringUtils.isNotBlank(works)){ writer.setRepresentativeWork(works.replace("：",":").split(":")[1].trim().replace(" ","/")); }
                                logger.info(writer.toString());
                                writers.add(writer);
                            });
                        }
                        movie.setWriters(writers);
                    }
                });
            }
        }catch (Exception ex){
            logger.error(ExceptionUtils.getStackTrace(ex));
        }finally {
            webDriver.close();
        }
        logger.info(movie.toString());
        return movie;
    }


}
