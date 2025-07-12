package com.nntk.m2s.repository;

import com.nntk.m2s.pojo.form.MapQueryBoundForm;
import com.nntk.m2s.pojo.po.GeoProvincePO;
import jakarta.annotation.Resource;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.geo.GeoBox;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.*;

import java.util.List;

//@Repository
public class GeoSearchRepository {

    @Resource
    private ElasticsearchOperations elasticsearchOperations;


    public List<GeoProvincePO> searchWithinDistance(double lat, double lon, double distance, String unit) {

        // 1. 创建地理距离查询
        Criteria criteria = new Criteria("location")
                .within(new GeoPoint(lat, lon), distance + unit);

        // 2. 构建查询
        Query query = new CriteriaQuery(criteria);

        // 3. 执行搜索
        SearchHits<GeoProvincePO> hits = elasticsearchOperations.search(query, GeoProvincePO.class);
        return hits.stream().map(SearchHit::getContent).toList();
    }

    public List<GeoProvincePO> searchWithinBounds(MapQueryBoundForm form) {
         // 1. 创建地理距离查询
//        GeoBoundingBoxQuery geoBoundingBoxQuery = QueryBuilders.geoBoundingBox()
//                .field("location")
//                .boundingBox(new GeoBounds.Builder()
//                        .tlbr(new TopLeftBottomRightGeoBounds.Builder()
//                                .topLeft(new GeoLocation.Builder()
//                                        .latlon(new LatLonGeoLocation.Builder()
//                                                .lat(form.getLeftLatitude())
//                                                .lon(form.getLeftLongitude())
//                                                .build())
//                                        .build())
//                                .bottomRight(new GeoLocation.Builder()
//                                        .latlon(new LatLonGeoLocation.Builder()
//                                                .lat(form.getRightLatitude())
//                                                .lon(form.getRightLongitude())
//                                                .build())
//                                        .build())
//                                .build())
//
//                        .build())
//
//                .build();


        Criteria criteria = new Criteria("location")
                .boundedBy(
                        new GeoBox(
                                new GeoPoint(form.getLeftLatitude(), form.getLeftLongitude()),
                                new GeoPoint(form.getRightLatitude(), form.getRightLongitude())
                        )
                );

        // 2. 构建查询
        Query query = new CriteriaQuery(criteria);

        // 3. 执行搜索
        SearchHits<GeoProvincePO> hits = elasticsearchOperations.search(query, GeoProvincePO.class);
        return hits.stream().map(SearchHit::getContent).toList();
    }
}
