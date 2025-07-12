package com.nntk.m2s.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "t_province", createIndex = true)
public class GeoProvincePO {
    @Id
    Long id;
    @Field(type = FieldType.Text)
    String name;

    @Field(type = FieldType.Text)
    String code;

    @GeoPointField
    GeoPoint location;


}