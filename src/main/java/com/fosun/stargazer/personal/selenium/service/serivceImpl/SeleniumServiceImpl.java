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
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Override
    public Set<String> getMovieLinks(String originUrl) {
        WebDriver webDriver = new ChromeDriver();
        Set<String> urlLinks = new HashSet<>();
        try{
            webDriver.get(originUrl);
            webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            List<WebElement> webElements = webDriver.findElements(By.xpath("//div[@class='list-wp']//div[@class='list']//a[@class='item']"));
            while(null != webElements){
                webElements.parallelStream().forEach(x->{
                    String url = x.getAttribute("href");
                    System.out.println(url);
                    urlLinks.add(url);
                });
            }
            //获取详情
//            List<Movie> movies = new ArrayList<>();
            if(!urlLinks.isEmpty()){
                urlLinks.parallelStream().forEach(x->{
                    Movie movie = getMovieDetailInfo(x);
                    if(null != movie){
                        neo4jService.insertMovie(movie);
                    }
                });
            }
            //点击更多
            webDriver.findElement(By.xpath("a[@class='more']")).click();
//            List<WebElement> webElements = webDriver.findElements(By.xpath("//div[@class='list-wp']//div[@class='list']//a[@class='item']"));


        }catch (Exception ex){
            logger.error(ExceptionUtils.getStackTrace(ex));
            try{
                //指定了OutputType.FILE做为参数传递给getScreenshotAs()方法，其含义是将截取的屏幕以文件形式返回。
                File srcFile = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
                //利用FileUtils工具类的copyFile()方法保存getScreenshotAs()返回的文件对象
                String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));  //当前时间
                String screenshotsFile = new File("").getParentFile().getParent() + System.getProperty(File.separator,"/") + curTime + "_screenshots.png";
                File file = CommonUtil.createFile(screenshotsFile);
                if(file.isFile()){
                    FileUtils.copyFile(srcFile, file);
                }
            }catch (Exception e){
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        webDriver.close();
        return urlLinks;

    }



    //多线程计算
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
            WebElement element = webDriver.findElement(By.xpath("//div[@id='content']")).findElement(By.tagName("h1"));
            //获取电影名
            String movieName = element.getText();
            logger.info("movie name :" + movieName);

            String[] arr = CommonUtil.regexMovieNameAndYearInfo(movieName);
            movie.setName(arr[0]); //电影名
            if(StringUtils.isNotBlank(arr[1])){ movie.setYear(Integer.valueOf(arr[1]));  }//电影的发行年份

            String infos = element.findElement(By.xpath("//div[@id='info']")).getText();
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

            String scoreDetail = element.findElement(By.xpath("//div[@class='ratings-on-weight']")).getText();
            Pattern newPatten = Pattern.compile("(\\d+星)\\D+(\\d+[.]?\\d+%)");
            Matcher newMatcher = newPatten.matcher(scoreDetail);
            String detail = "";
            while(newMatcher.find()){
                detail += newMatcher.group(1) + ":" + newMatcher.group(2)+",";
            }
            if(StringUtils.isNotBlank(detail) && detail.length() >1){
                movie.setScoreDetail(detail.substring(0,detail.length()-1));  //评分详情
            }

            String score = element.findElement(By.xpath("//div[@class='rating_self clearfix']//strong[@class='ll rating_num']")).getText();
            String num =element.findElement(By.xpath("//div[@class='rating_sum']//span[@property='v:votes']")).getText(); //评价的人数
            movie.setScore(StringUtils.isNotBlank(score)? Double.valueOf(score):0.0);
            movie.setCommentNumber(StringUtils.isNotBlank(num)? Integer.valueOf(num):0);


            //查看演员信息
            element = element.findElement(By.xpath("//div[@id='celebrities']//span[@class='pl']"));
            element.click();
            List<WebElement> elementList = webDriver.findElements(By.xpath("//div[@id ='celebrities']//div[@class='list-wrapper']"));

            if(null != elementList){
                elementList.parallelStream().forEach(x->{
                    String roleName = x.findElement(By.tagName("h2")).getText();
                    if(roleName.contains("导演")){
                        String name = x.findElement(By.cssSelector(".info .name")).getText(); //人名
                        String works = x.findElement(By.cssSelector(".info .works")).getText(); //代表作

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
                                String name = actorInfo.findElement(By.cssSelector(".info .name")).getText(); //人名
                                String role = actorInfo.findElement(By.cssSelector(".info .role")).getText(); //角色
                                String works = actorInfo.findElement(By.cssSelector(".info .works")).getText(); //代表作
                                if(role.contains("饰")){ //如果有角色
                                    role = role.substring(role.indexOf("饰")+1,role.lastIndexOf(")")).trim(); //提取扮演的角色
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
                                String name = writerInfo.findElement(By.cssSelector(".info >.name")).getText(); //人名
                                String works = writerInfo.findElement(By.cssSelector(".info >.works")).getText(); //代表作
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


    public static void main(String[] args){
        String scoreDetail= "5星\n13.3%\n4星\n3.2%\n3星\n34.8%\n2星\n6.8%\n1星\n18%";
        String[] scores = scoreDetail.split("\n");
        String res = "";
        for(int i = 0; i<scores.length;i++){
            if(i%2==0){
                res += scores[i]+":";
            }else{
                res += scores[i] + ",";
            }
        }
        System.out.println("res:" + res.substring(0,res.length()-1));
        Pattern newPatten = Pattern.compile("(\\d+星)\\D+(\\d+[.]?\\d+%)");
        Matcher newMatcher = newPatten.matcher(scoreDetail);
        String str = "";
        while(newMatcher.find()){
            str += newMatcher.group(1) + ":" + newMatcher.group(2)+",";
        }
        System.out.println("pattern str:" + str);


        String name = "adb dd ee";
        System.out.println(name.split(" ",2)[0] + "----" + name.split(" ",2)[1]);


        String betterThan = "好于 61% 喜剧片\n好于 39% 剧情片";
        if(StringUtils.isNotBlank(betterThan)){
//             Pattern doublePattern = Pattern.compile("\\D+(\\d+[.]?\\d+%)\\D+("+ "剧情"+")");
            Pattern doublePattern = Pattern.compile("\\D+([0-9.%]+)\\D+("+ "剧情"+")");
             Matcher matcher = doublePattern.matcher(betterThan);
            if(matcher.find()){
                System.out.println(matcher.group(1));
            }
        }

        String role = "演员 Actor/Actress (饰 马进)";
        String works = "代表作：人再囧途之泰囧/西游降魔篇".replace("：",":");
        role = role.substring(role.indexOf("饰")+1,role.lastIndexOf(")")).trim();
        System.out.println("works:" + works.split(":")[1]);
        System.out.println("role:" + role);
        String movieName = "血战硫磺岛(2013)";
        String pattern = "(\\D*)\\((\\d{4})(.*)";
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);
        // 现在创建 matcher 对象
        Matcher m = r.matcher(movieName);
        if(m.find()) {
            movieName = m.group(1);
            String year = m.group(2);
            System.out.println("movieName:" + movieName + ", year:" + year);
        }

    }
}
