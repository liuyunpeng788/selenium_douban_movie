package com.fosun.stargazer.personal.selenium.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SeleniumUtil {
    /**
     * 在指定的节点下，查找定位字符串所在的节点元素
     * @param element 节点元素
     * @param byArray some By which locates elements by the value of the "id" attribute. ( 一组给定id属性、用于定位的By,可以是多个)
     * @return 节点元素。如果为空，表示没有找到，否则，返回当前找到的节点元素
     * @description 因为findElement会抛出异常，所以用该方法处理
     */
    public static WebElement getWebElement(final WebElement element, By... byArray){
        if(null == element || null == byArray){
            return element;
        }

        WebElement res = element;
        try{
            for(By by : byArray){
                res = res.findElement(by);
            }
        }catch (Exception ex){
            res = null;
        }
       return res;
    }
    /**
     * 获取元素的属性值
     * @param element 当前待查找的元素
     * @param byArray 查找的方式/条件
     * @return 查找的结果
     **/
    public static String getWebElementText(final WebElement element, By... byArray){
        WebElement webElement = getWebElement(element,byArray);
        return null == webElement? null:webElement.getText();
    }

    /**
     * 获取元素的属性值
     * @param element 当前待查找的元素
     * @param byArray 查找的方式/条件
     * @param attrName 属性名
     * @return 属性值
     */
    public static String getWebElementAttributeValue(final WebElement element,String attrName, By... byArray){
        WebElement webElement = getWebElement(element,byArray);
        return null == webElement? null:webElement.getAttribute(attrName);
    }

    public static WebElement getWebElementByWebDriver(final WebDriver webDriver, By... byArray){
        if(null == webDriver || null == byArray ){
            return null;
        }
        WebElement webElement = null;
        try{
            int i = 0;
            for(By by : byArray){
                 webElement = i == 0 ?webDriver.findElement(by):webElement.findElement(by);
                 i++;
            }
        }catch (Exception ex){
            webElement = null;
        }
        return webElement;
    }
}
