//package com.nntk.m2s.repository;
//
//
//import com.nntk.m2s.pojo.po.GeoProvincePO;
//import org.apache.ibatis.annotations.Param;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.data.geo.Distance;
//import org.springframework.data.geo.Point;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
////@Repository
//public interface GeoProvinceRepository extends ElasticsearchRepository<GeoProvincePO, Long> {
//    /**
//     * 根据范围获取数据
//     *
//     * @param point    x y 对应经纬度
//     * @param distance 范围
//     * @return
//     */
//    List<GeoProvincePO> findByLocationNear(@Param("location") Point point, @Param("distance") Distance distance);
//
//
//}