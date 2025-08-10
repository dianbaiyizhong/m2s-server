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
 * @since 2025-08-10
 */
@TableName("t_country")
public class TCountry implements Serializable {

        private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String enName;

    private String intro;

    private String intro2;

    private String countryCode;

    private String code;

    private Double lng;

    private Double lat;

      /**
     * 图片链接
     */
      private String imageUrl;

    private String flagRes;

    private String locale;

    private String emoji;

    private Byte enable;
    
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
    
    public String getEnName() {
        return enName;
    }

      public void setEnName(String enName) {
          this.enName = enName;
      }
    
    public String getIntro() {
        return intro;
    }

      public void setIntro(String intro) {
          this.intro = intro;
      }
    
    public String getIntro2() {
        return intro2;
    }

      public void setIntro2(String intro2) {
          this.intro2 = intro2;
      }
    
    public String getCountryCode() {
        return countryCode;
    }

      public void setCountryCode(String countryCode) {
          this.countryCode = countryCode;
      }
    
    public String getCode() {
        return code;
    }

      public void setCode(String code) {
          this.code = code;
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
    
    public String getFlagRes() {
        return flagRes;
    }

      public void setFlagRes(String flagRes) {
          this.flagRes = flagRes;
      }
    
    public String getLocale() {
        return locale;
    }

      public void setLocale(String locale) {
          this.locale = locale;
      }
    
    public String getEmoji() {
        return emoji;
    }

      public void setEmoji(String emoji) {
          this.emoji = emoji;
      }
    
    public Byte getEnable() {
        return enable;
    }

      public void setEnable(Byte enable) {
          this.enable = enable;
      }

    @Override
    public String toString() {
        return "TCountry{" +
                  "id = " + id +
                      ", name = " + name +
                      ", enName = " + enName +
                      ", intro = " + intro +
                      ", intro2 = " + intro2 +
                      ", countryCode = " + countryCode +
                      ", code = " + code +
                      ", lng = " + lng +
                      ", lat = " + lat +
                      ", imageUrl = " + imageUrl +
                      ", flagRes = " + flagRes +
                      ", locale = " + locale +
                      ", emoji = " + emoji +
                      ", enable = " + enable +
                  "}";
    }
}
