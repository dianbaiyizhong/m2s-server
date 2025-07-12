package com.nntk.m2s.mp.generate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author nntk
 * @since 2025-07-12
 */
@TableName("t_city")
public class TCity implements Serializable {

        private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String cityCode;

    private String areaCode;

    private String provinceShortHand;

    private String licensePlateNum;

    private Double lat;

    private Double lng;

    private String imageUrl;

    private String intro2;

    private String intro;
    
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
    
    public String getCityCode() {
        return cityCode;
    }

      public void setCityCode(String cityCode) {
          this.cityCode = cityCode;
      }
    
    public String getAreaCode() {
        return areaCode;
    }

      public void setAreaCode(String areaCode) {
          this.areaCode = areaCode;
      }
    
    public String getProvinceShortHand() {
        return provinceShortHand;
    }

      public void setProvinceShortHand(String provinceShortHand) {
          this.provinceShortHand = provinceShortHand;
      }
    
    public String getLicensePlateNum() {
        return licensePlateNum;
    }

      public void setLicensePlateNum(String licensePlateNum) {
          this.licensePlateNum = licensePlateNum;
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
    
    public String getImageUrl() {
        return imageUrl;
    }

      public void setImageUrl(String imageUrl) {
          this.imageUrl = imageUrl;
      }
    
    public String getIntro2() {
        return intro2;
    }

      public void setIntro2(String intro2) {
          this.intro2 = intro2;
      }
    
    public String getIntro() {
        return intro;
    }

      public void setIntro(String intro) {
          this.intro = intro;
      }

    @Override
    public String toString() {
        return "TCity{" +
                  "id = " + id +
                      ", name = " + name +
                      ", cityCode = " + cityCode +
                      ", areaCode = " + areaCode +
                      ", provinceShortHand = " + provinceShortHand +
                      ", licensePlateNum = " + licensePlateNum +
                      ", lat = " + lat +
                      ", lng = " + lng +
                      ", imageUrl = " + imageUrl +
                      ", intro2 = " + intro2 +
                      ", intro = " + intro +
                  "}";
    }
}
