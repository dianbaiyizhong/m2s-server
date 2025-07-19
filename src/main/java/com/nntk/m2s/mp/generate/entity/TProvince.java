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
 * @since 2025-07-19
 */
@TableName("t_province")
public class TProvince implements Serializable {

        private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String intro;

    private String code;

    private String shorthand;

    private String bgColor;

    private String enBgColor;

    private Double lng;

    private Double lat;

      /**
     * 图片链接
     */
      private String imageUrl;

      /**
     * 新冠肺炎口号
     */
      private String ncpSlogan;
    
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
    
    public String getIntro() {
        return intro;
    }

      public void setIntro(String intro) {
          this.intro = intro;
      }
    
    public String getCode() {
        return code;
    }

      public void setCode(String code) {
          this.code = code;
      }
    
    public String getShorthand() {
        return shorthand;
    }

      public void setShorthand(String shorthand) {
          this.shorthand = shorthand;
      }
    
    public String getBgColor() {
        return bgColor;
    }

      public void setBgColor(String bgColor) {
          this.bgColor = bgColor;
      }
    
    public String getEnBgColor() {
        return enBgColor;
    }

      public void setEnBgColor(String enBgColor) {
          this.enBgColor = enBgColor;
      }
    
    public Double getLng() {
        return lng;
    }

      public void setLng(Double lng) {
          this.lng = lng;
      }
    
    public Double getLat() {
        return lat;
    }

      public void setLat(Double lat) {
          this.lat = lat;
      }
    
    public String getImageUrl() {
        return imageUrl;
    }

      public void setImageUrl(String imageUrl) {
          this.imageUrl = imageUrl;
      }
    
    public String getNcpSlogan() {
        return ncpSlogan;
    }

      public void setNcpSlogan(String ncpSlogan) {
          this.ncpSlogan = ncpSlogan;
      }

    @Override
    public String toString() {
        return "TProvince{" +
                  "id = " + id +
                      ", name = " + name +
                      ", intro = " + intro +
                      ", code = " + code +
                      ", shorthand = " + shorthand +
                      ", bgColor = " + bgColor +
                      ", enBgColor = " + enBgColor +
                      ", lng = " + lng +
                      ", lat = " + lat +
                      ", imageUrl = " + imageUrl +
                      ", ncpSlogan = " + ncpSlogan +
                  "}";
    }
}
