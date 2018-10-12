package com.fosun.stargazer.personal.selenium;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StaticFunctionTest {
    /**
     * @Test
     * 用于测试异常状态下保存参数页面截图是否成功
     */
    private static void saveScreenShot(){
            WebDriver webDriver = new ChromeDriver();
        try{
            webDriver.get("https://movie.douban.com/explore");
            //指定了OutputType.FILE做为参数传递给getScreenshotAs()方法，其含义是将截取的屏幕以文件形式返回。
            File srcFile = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
            //利用FileUtils工具类的copyFile()方法保存getScreenshotAs()返回的文件对象

            String curTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));  //当前时间
            String path = new File(".").getCanonicalPath() + File.separator + "screenshots"  ;
            String filename = path + File.separator + curTime + "_screenshots.png";

            File dir = new File(path);
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file = new File(filename);
            if(!file.exists()){
                file.createNewFile();
            }
            FileUtils.copyFile(srcFile, file);
         }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            webDriver.close();
        }
    }

    public static void regexRoleTest(){
        //查找角色
        String role = "饰 坏蛋";
        role = role.replaceAll("[()（）]]","").trim();
        System.out.println(role.split("饰")[1].trim());

        role = "饰 (坏蛋)";
        role = role.replaceAll("[()（）]","").trim();
        System.out.println(role.split("饰")[1].trim());

        role = "(饰 坏蛋）";
        role = role.replaceAll("[()（）]","").trim();
        System.out.println(role.split("饰")[1].trim());

    }

    //评分提取测试
    public static void regexCommentScoreTest(){
        //split method
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

        //regex method
        Pattern newPatten = Pattern.compile("(\\d+星)\\D+(\\d+[.]?\\d+%)");
        Matcher newMatcher = newPatten.matcher(scoreDetail);
        String str = "";
        while(newMatcher.find()){
            str += newMatcher.group(1) + ":" + newMatcher.group(2)+",";
        }
        System.out.println("pattern str:" + str);

    }

    //split 参数方法测试
    public static void splitTest(){
        //test split param
        String name = "adb dd ee";
        System.out.println(name.split(" ",2)[0] + "----" + name.split(" ",2)[1]);

    }

    //提取同类型电影比较数据测试
    public static void regexBetterThanInfoTest(){
        String betterThan = "好于 61% 喜剧片\n好于 39% 剧情片";
        if(StringUtils.isNotBlank(betterThan)){
//             Pattern doublePattern = Pattern.compile("\\D+(\\d+[.]?\\d+%)\\D+("+ "剧情"+")");
            Pattern doublePattern = Pattern.compile("\\D+([0-9.%]+)\\D+("+ "剧情"+")");
            Matcher matcher = doublePattern.matcher(betterThan);
            if(matcher.find()){
                System.out.println(matcher.group(1));
            }
        }
    }

    //提取电影名称和年份测试
    public static void regexMovieNameAndYearTest(){
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

    public static void main(String[] args){
        saveScreenShot();

    }
}
