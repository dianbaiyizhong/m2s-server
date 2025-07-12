package com.nntk.m2s.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MapNewsCoverVo {

    private Integer id;

    private String title;

    private String coverUrl;

    private String createTime;

}
