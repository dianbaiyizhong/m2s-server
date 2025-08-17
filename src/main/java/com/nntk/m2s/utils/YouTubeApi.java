package com.nntk.m2s.utils;

import cn.hutool.system.SystemUtil;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.*;

@Slf4j
public class YouTubeApi {
    private static final String DRIVER_PATH = "D:\\software\\chromedriver.exe";

    static {
        if (SystemUtil.getOsInfo().isWindows()) {
            System.setProperty("webdriver.chrome.driver", DRIVER_PATH);
        } else {
            System.setProperty("webdriver.chrome.driver", "/Users/huanghaoming/Downloads/chromedriver-mac-arm64/chromedriver");
        }
    }


    public static String getPlayUrl() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);

        String youtubeEnterUrl = "https://www.youtube.com/results?search_query=" + "新闻联播";

        driver.get(youtubeEnterUrl);
        String pageSource = driver.getPageSource();
        Element content = Jsoup.parse(pageSource).select("#content").first();

        Elements elements = content.getElementsByTag("ytd-video-renderer");

        for (Element element : elements) {

            try {
                Map<String, String> map = new HashMap<>();
                String url = element.select("#video-title").attr("href");
                driver.close();
                return "https://www.youtube.com" + url;
            } catch (Exception e) {
                log.warn("spider warn:", e);
            }
            break;
        }
        driver.close();
        return null;
    }

}
