package com.nntk.m2s;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.nntk.m2s.mp.generate.entity.TCity;
import com.nntk.m2s.mp.generate.entity.TCountry;
import com.nntk.m2s.mp.generate.entity.TDistinct;
import com.nntk.m2s.mp.generate.mapper.TCityMapper;
import com.nntk.m2s.mp.generate.mapper.TCountryMapper;
import com.nntk.m2s.mp.generate.mapper.TDistinctMapper;
import com.nntk.m2s.service.IAiService;
import com.nntk.m2s.service.INewsService;
import com.nntk.m2s.service.ISpiderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class ServiceTest {


    @Autowired
    private INewsService newsService;

    @Autowired
    private ISpiderService ISpiderService;

    @Test
    void contextLoads() {

        newsService.getMapNewsCoverList(1, 10);

    }

    @Test
    public void spider() {
        ISpiderService.spiderI18nNews();

    }


    @Autowired
    private TDistinctMapper distinctMapper;

    @Autowired
    private TCityMapper cityMapper;


    @Autowired
    private IAiService aiService;


    @Autowired
    private TCountryMapper countryMapper;


    @Test
    public void updateCountryInfo() throws IOException {

        List<TCountry> tCountries = countryMapper.selectList(null);
        for (int i = 0; i < tCountries.size(); i++) {
            TCountry country = tCountries.get(i);

            try {

                String prompt = tCountries.get(i) + """
                        。请把我输出这个国家的经纬度，一定要精确，输出json格式，属性分别为lat，lng.
                        """;

                System.out.println(prompt);
                String bailianResponse = aiService.getBailianResponse(prompt);


                JSONObject jsonObject = JSON.parseObject(bailianResponse);
                country.setLat(jsonObject.getDouble("lat"));
                country.setLng(jsonObject.getDouble("lng"));
                System.out.println(bailianResponse);
                countryMapper.updateById(country);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(country);
            }

        }

    }

    @Test
    public void updateCityInfo() throws IOException {

        List<TCity> tCities = cityMapper.selectList(null);


        for (int i = 0; i < tCities.size(); i++) {
            TCity tCity = tCities.get(i);
            String prompt = tCity.getName() + """
                    。请把我输出这个城市的经纬度，输出json格式，一定要精确,属性分别为lat，lng.
                    """;
            String bailianResponse = aiService.getBailianResponse(prompt);
            JSONObject jsonObject = JSON.parseObject(bailianResponse);
            tCity.setLat(jsonObject.getDouble("lat"));
            tCity.setLng(jsonObject.getDouble("lng"));
            cityMapper.updateById(tCity);
        }
    }


    @Test
    public void loadGeoData() throws IOException {
        Workbook wb = new XSSFWorkbook(ResourceUtil.getStream("AMap_adcode_citycode.xlsx"));
        Sheet sheet0 = wb.getSheetAt(0);
        //获取第一张表
        for (int i = 0; i <= sheet0.getLastRowNum(); i++) {
            Row row = sheet0.getRow(i);
            String name = row.getCell(0).getStringCellValue();
            String adcode = row.getCell(1).toString();
            Cell citycode_cell = row.getCell(2);
            String centerNum = adcode.substring(2, 4);
            String rightNum = adcode.substring(4, 6);
            String cityCode = row.getCell(2).toString();

            if (adcode.equals("710000")) {
                log.info(name + " - " + adcode);

                continue;
            }

            if (citycode_cell == null) {
                log.info("省份:" + name + " - " + adcode);

            } else {
                if (rightNum.equals("00") && !centerNum.equals("00")) {

                    TCity tCity = cityMapper.selectOne(new QueryWrapper<TCity>().lambda().eq(TCity::getName, name).eq(TCity::getCityCode, cityCode));
                    if (tCity == null) {
                        tCity = new TCity();
                        tCity.setName(name);
                        tCity.setAreaCode(adcode);
                        tCity.setCityCode(cityCode);
                        tCity.setUpdateTime(LocalDateTime.now());
                        cityMapper.insert(tCity);
                        log.info("城市:" + name + " - " + cityCode);
                    } else {
                        tCity.setUpdateTime(LocalDateTime.now());
                        cityMapper.updateById(tCity);
                    }

                }

                if (!centerNum.equals("00") && !rightNum.equals("00") && !rightNum.equals("01")) {

                    TDistinct tDistinct = distinctMapper.selectOne(new QueryWrapper<TDistinct>().lambda().eq(TDistinct::getName, name).eq(TDistinct::getAdCode, adcode));

                    if (tDistinct == null) {
                        log.info("县城:" + name + " - " + adcode);
                        tDistinct = new TDistinct();
                        tDistinct.setAdCode(adcode);
                        tDistinct.setName(name);
                        tDistinct.setCityCode(cityCode);
                        tDistinct.setUpdateTime(LocalDateTime.now());
                        // distinctMapper.insert(tDistinct);
                    } else {
                        tDistinct.setUpdateTime(LocalDateTime.now());
                        // distinctMapper.updateById(tDistinct);
                    }

                    // 执行完成后，update_time为null或者不是最新的，都是没有的数据，手动删掉即可

                }


            }
        }
    }
}
