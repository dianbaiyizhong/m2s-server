//package com.nntk.m2s;
//
//import com.nntk.m2s.mp.generate.entity.TProvince;
//import com.nntk.m2s.mp.generate.mapper.TProvinceMapper;
//import com.nntk.m2s.pojo.form.MapQueryBoundForm;
//import com.nntk.m2s.repository.GeoSearchRepository;
//import com.nntk.m2s.repository.GeoProvinceRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//@SpringBootTest
//class InitDataTest {
//
//
//    @Autowired
//    private GeoProvinceRepository repository;
//
//    @Autowired
//    private TProvinceMapper provinceMapper;
//
//    @Autowired
//    private GeoSearchRepository geoSearchRepository;
//
//    @Test
//    void contextLoads() {
//
//        List<TProvince> tProvinces = provinceMapper.selectList(null);
//
//        for (TProvince tProvince : tProvinces) {
////            repository.save(new GeoProvincePO(tProvince.getId().longValue(), tProvince.getName(), tProvince.getCode(), new GeoPoint(tProvince.getLat(), tProvince.getLng())));
//        }
//
////        List<GeoProvincePO> byLocationNear = repository.findByLocationNear(new Point(116, 39.2), new Distance(300, Metrics.KILOMETERS));
////        System.out.println(byLocationNear.size());
////
////
////        geoSearchService.searchWithinDistance(39, 116, 300, "km")
////                .forEach(System.out::println);
//
//
//        geoSearchRepository.searchWithinBounds(MapQueryBoundForm.builder()
//                        .leftLatitude(45.03441847527195)
//                        .leftLongitude(113.06230038699599)
//                        .rightLatitude(36.5421561524963)
//                        .rightLongitude(118.89259254194133)
//                        .build())
//                .forEach(System.out::println);
//
//    }
//
//}
