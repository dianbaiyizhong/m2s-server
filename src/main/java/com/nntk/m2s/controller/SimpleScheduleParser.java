package com.nntk.m2s.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.nntk.m2s.pojo.bo.GameScheduleInfo;

import java.text.SimpleDateFormat;
import java.util.*;

public class SimpleScheduleParser {

    public List<GameScheduleInfo> parseSchedule(String json) {
        List<GameScheduleInfo> gameList = new ArrayList<>();

        // 读取文件内容
        String content = json;

        // 解析JSON
        JSONObject rootObject = JSON.parseObject(content);
        JSONArray events = rootObject.getJSONArray("events");
        String starTeam = rootObject.getJSONObject("team").getString("name").toLowerCase(Locale.ROOT).replaceAll(" ", "");

        if (events != null) {
            for (int i = 0; i < events.size(); i++) {
                JSONObject event = events.getJSONObject(i);
                GameScheduleInfo gameInfo = parseEvent(event);
                if (gameInfo != null) {
                    if (gameInfo.getGuestTeam().equals(starTeam)) {
                        gameInfo.setCompetitor(gameInfo.getHomeTeam());
                    } else {
                        gameInfo.setCompetitor(gameInfo.getGuestTeam());
                    }
                    gameList.add(gameInfo);
                }
            }
        }

        return gameList;
    }

    private GameScheduleInfo parseEvent(JSONObject event) {
        try {
            // 解析时间
            String dateTimeStr = event.getString("date");
            String formattedTime = formatDateTime(dateTimeStr);

            // 解析比赛信息
            JSONArray competitions = event.getJSONArray("competitions");
            if (competitions == null || competitions.isEmpty()) {
                return null;
            }

            JSONObject competition = competitions.getJSONObject(0);
            JSONArray competitors = competition.getJSONArray("competitors");

            if (competitors == null || competitors.size() < 2) {
                return null;
            }

            String homeTeam = "";
            String guestTeam = "";
            String homeRate = "";
            String guestRate = "";

            // 解析主客队和比分
            for (int i = 0; i < competitors.size(); i++) {
                JSONObject competitor = competitors.getJSONObject(i);
                String homeAway = competitor.getString("homeAway");
                JSONObject team = competitor.getJSONObject("team");
                String teamName = team.getString("shortDisplayName").toLowerCase(Locale.ROOT);
                if ("home".equals(homeAway)) {
                    homeTeam = teamName;
                } else if ("away".equals(homeAway)) {
                    guestTeam = teamName;
                }
                if (!competitor.containsKey("score")) {
                    continue;
                }
                String score = competitor.getJSONObject("score").getString("displayValue");
                if ("home".equals(homeAway)) {
                    homeTeam = teamName;
                    homeRate = score;
                } else if ("away".equals(homeAway)) {
                    guestTeam = teamName;
                    guestRate = score;
                }
            }

            return new GameScheduleInfo(formattedTime, guestTeam.replaceAll(" ", ""), homeTeam.replaceAll(" ", ""), homeRate, guestRate, null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String formatDateTime(String dateTimeStr) {
        try {
            // 原始格式：2025-10-22T02:00Z
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
            inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 北京时间时区

            Date date = inputFormat.parse(dateTimeStr);
            return outputFormat.format(date);

        } catch (Exception e) {
            return dateTimeStr; // 如果解析失败，返回原始字符串
        }
    }


}
