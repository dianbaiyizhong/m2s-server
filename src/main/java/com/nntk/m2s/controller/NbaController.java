package com.nntk.m2s.controller;

import com.alibaba.fastjson2.JSON;
import com.nntk.m2s.mp.custom.entity.MapNewsCoverDTO;
import com.nntk.m2s.mp.generate.entity.TNbaMedia;
import com.nntk.m2s.mp.generate.mapper.TNbaMediaMapper;
import com.nntk.m2s.pojo.bo.GameInfo;
import com.nntk.m2s.pojo.bo.GameScheduleInfo;
import com.nntk.m2s.pojo.form.BasePageForm;
import com.nntk.m2s.pojo.form.MoreNewsForm;
import com.nntk.m2s.pojo.form.NewsRequestForm;
import com.nntk.m2s.pojo.vo.NewsVo;
import com.nntk.m2s.result.PageResult;
import com.nntk.m2s.result.RespBodyBuilder;
import com.nntk.m2s.result.ResultDataVo;
import com.nntk.m2s.service.INewsService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "nba")
@ResponseBody
public class NbaController {



    @Autowired
    private TNbaMediaMapper nbaMediaMapper;

    @GetMapping("/getMedia")
    public ResultDataVo getMedia() {

        List<TNbaMedia> tNbaMedia = nbaMediaMapper.selectList(null);
        return RespBodyBuilder.success(tNbaMedia);

    }

    @GetMapping("/getCalendar/{teamId}")
    @Cacheable(value = "getCalendar", key = "#teamId")
    public ResultDataVo getCalendar(@PathVariable("teamId") int teamId) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url("https://site.api.espn.com/apis/site/v2/sports/basketball/nba/teams/%s/schedule".formatted(teamId))
                .build();
        Call call = client.newCall(request);

        Response response = call.execute();
        if (response.isSuccessful()) {
            String html = response.body().string();
            SimpleScheduleParser simpleScheduleParser = new SimpleScheduleParser();
            List<GameScheduleInfo> gameScheduleInfos = simpleScheduleParser.parseSchedule(html);
            List<GameScheduleInfo> result = new ArrayList<>();

            LocalDate first = LocalDate.parse(gameScheduleInfos.get(0).getTime());
            LocalDate previousSunday = getPreviousSunday(first);
            List<String> dateRange = generateDateRange(previousSunday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), gameScheduleInfos.get(gameScheduleInfos.size() - 1).getTime());

            for (int i = 0; i < dateRange.size(); i++) {

                String currentDate = dateRange.get(i);
                GameScheduleInfo gamesOnDate = gameScheduleInfos.stream()
                        .filter(game -> game.getTime().equals(currentDate))
                        .findFirst().orElseGet(() -> null);
                if (gamesOnDate == null) {
                    result.add(GameScheduleInfo.builder()
                            .time(currentDate)
                            .build());
                } else {
                    result.add(gamesOnDate);
                }

            }


            return RespBodyBuilder.success(result);
        }
        return RespBodyBuilder.success();
    }


    public static LocalDate getPreviousSunday(LocalDate date) {
        // 获取当前日期是星期几（1=周一, 7=周日）
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // 计算距离上一个周日的天数差
        // 如果当天是周日(7)，差值为0；否则计算需要回溯的天数
        int daysToSubtract = dayOfWeek.getValue() == 7 ? 0 : dayOfWeek.getValue();
        return date.minusDays(daysToSubtract);
    }

    // 使用Stream API的版本
    public static List<String> generateDateRange(String startDate, String endDate) {
        List<String> dateList = new ArrayList<>();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            // 生成日期范围
            while (!start.isAfter(end)) {
                dateList.add(start.format(formatter));
                start = start.plusDays(1);
            }

        } catch (Exception e) {
            System.err.println("日期解析错误: " + e.getMessage());
            e.printStackTrace();
        }

        return dateList;
    }


    @GetMapping("/getGame")
    public ResultDataVo list() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url("https://nba.hupu.com/games")
                .build();
        Call call = client.newCall(request);
        List<GameInfo> gameInfoList = new ArrayList<>();

        try {
            //同步发送请求
            Response response = call.execute();
            if (response.isSuccessful()) {
                String html = response.body().string();

                Elements cardTeams = Jsoup.parse(html).select("div.gamecenter_content   div.list_box  div.team_vs_a");
                for (Element card : cardTeams) {


                    Elements game = card.select("div.txt");
                    String guestTeam = null;
                    String guestRate = null;
                    String homaTeam = null;
                    String homaRate = null;


                    if (game.get(0).select("span").size() == 1) {
                        guestTeam = game.get(0).select("span").get(0).text().replaceAll("\\(\\d+\\)", "").trim();

                        guestRate = "-";
                    } else {
                        guestTeam = game.get(0).select("span").get(1).text().replaceAll("\\(\\d+\\)", "").trim();

                        guestRate = game.get(0).select("span").get(0).text().replaceAll("\\(\\d+\\)", "").trim();

                    }
                    if (game.get(1).select("span").size() == 1) {
                        homaTeam = game.get(1).select("span").get(0).text().replaceAll("\\(\\d+\\)", "").trim();

                        homaRate = "-";
                    } else {
                        homaTeam = game.get(1).select("span").get(1).text().replaceAll("\\(\\d+\\)", "").trim();

                        homaRate = game.get(1).select("span").get(0).text().replaceAll("\\(\\d+\\)", "").trim();

                    }


                    gameInfoList.add(GameInfo.builder()
                            .guestRate(guestRate)
                            .guestTeam(guestTeam)
                            .homeRate(homaRate)
                            .homeTeam(homaTeam)
                            .build());
                }


            }
        } catch (Exception e) {
            return RespBodyBuilder.success(gameInfoList);
        }
        return RespBodyBuilder.success(gameInfoList);
    }


}
