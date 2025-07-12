package com.nntk.m2s.pojo.form;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MapQueryBoundForm {


    /**
     * 左上角
     */
    @NotNull
    private Double leftLongitude;
    @NotNull
    private Double leftLatitude;

    /**
     * 右下角
     */
    @NotNull
    private Double rightLongitude;
    @NotNull
    private Double rightLatitude;


    private Double currentLatitude;
    private Double currentLongitude;


    private float zoom;


}
