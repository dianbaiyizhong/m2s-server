package com.nntk.m2s.controller;

import com.nntk.m2s.mp.custom.entity.MapNewsCoverDTO;
import com.nntk.m2s.pojo.bo.GameInfo;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "nba")
@ResponseBody
public class NbaController {

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
                        ;
                        guestRate = "-";
                    } else {
                        guestTeam = game.get(0).select("span").get(1).text().replaceAll("\\(\\d+\\)", "").trim();
                        ;
                        guestRate = game.get(0).select("span").get(0).text().replaceAll("\\(\\d+\\)", "").trim();
                        ;
                    }
                    if (game.get(1).select("span").size() == 1) {
                        homaTeam = game.get(1).select("span").get(0).text().replaceAll("\\(\\d+\\)", "").trim();
                        ;
                        homaRate = "-";
                    } else {
                        homaTeam = game.get(1).select("span").get(1).text().replaceAll("\\(\\d+\\)", "").trim();
                        ;
                        homaRate = game.get(1).select("span").get(0).text().replaceAll("\\(\\d+\\)", "").trim();
                        ;
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
