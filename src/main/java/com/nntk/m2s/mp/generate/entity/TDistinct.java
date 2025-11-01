package com.nntk.m2s.mp.generate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author nntk
 * @since 2025-10-04
 */
@TableName("t_distinct")
public class TDistinct implements Serializable {

        private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private Double lat;

    private Double lng;

    private String adCode;

    private String cityCode;

    private LocalDateTime updateTime;
    
    public Integer getId() {
        return id;
    }

      public void setId(Integer id) {
          this.id = id;
      }
    
    public String getName() {
        return name;
    }

      public void setName(String name) {
          this.name = name;
      }
    
    public Double getLat() {
        return lat;
    }

      public void setLat(Double lat) {
          this.lat = lat;
      }
    
    public Double getLng() {
        return lng;
    }

      public void setLng(Double lng) {
          this.lng = lng;
      }
    
    public String getAdCode() {
        return adCode;
    }

      public void setAdCode(String adCode) {
          this.adCode = adCode;
      }
    
    public String getCityCode() {
        return cityCode;
    }

      public void setCityCode(String cityCode) {
          this.cityCode = cityCode;
      }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

      public void setUpdateTime(LocalDateTime updateTime) {
          this.updateTime = updateTime;
      }

    @Override
    public String toString() {
        return "TDistinct{" +
                  "id = " + id +
                      ", name = " + name +
                      ", lat = " + lat +
                      ", lng = " + lng +
                      ", adCode = " + adCode +
                      ", cityCode = " + cityCode +
                      ", updateTime = " + updateTime +
                  "}";
    }
}
