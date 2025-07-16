package com.nntk.m2s;

import cn.hutool.core.io.resource.ResourceUtil;
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

    @Test
    public void loadGeoData() throws IOException {
//        Workbook wb = new XSSFWorkbook(ResourceUtil.getStream("AMap_adcode_citycode.xlsx"));
//        Sheet sheet0 = wb.getSheetAt(0);
//        //获取第一张表
//        for (int i = 0; i <= sheet0.getLastRowNum(); i++) {
//            Row row = sheet0.getRow(i);
//            String name = row.getCell(0).getStringCellValue();
//            String adcode = row.getCell(1).toString();
//            Cell citycode_cell = row.getCell(2);
//            String centerNum = adcode.substring(2, 4);
//            String rightNum = adcode.substring(4, 6);
//
//            if (adcode.equals("710000")) {
//                log.info(name + " - " + adcode);
//            }
//
//            if (citycode_cell == null) {
//                log.info("省份:" + name + " - " + adcode);
//
//            } else {
//                if (rightNum.equals("00") && !centerNum.equals("00")) {
//                    log.info("城市:" + name + " - " + adcode);
//
//                }
//
//                if (!centerNum.equals("00") && !rightNum.equals("00") && !rightNum.equals("01")) {
//                    log.info("县城:" + name + " - " + adcode);
//
//                }
//
//
//            }
//        }
    }
}
