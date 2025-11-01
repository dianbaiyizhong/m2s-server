package com.nntk.m2s.mp.generate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author nntk
 * @since 2025-10-04
 */
@TableName("t_news")
public class TNews implements Serializable {

        private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer comboId;

    private String title;

    private Boolean mapNews;

    private Integer areaLevel;

    private Integer areaId;

    private LocalDateTime createTime;

    private LocalDateTime newsTime;

    private String locationSubtitle;

    private String videoUrl;

    private String thumbImg;

    private String sourceUrl;

    private String sourceName;

    private String newsContent;

    private String images;

    private LocalDate formattedNewsDate;

    private Integer newsType;

    private Double lat;

    private Double lng;
    
    public Integer getId() {
        return id;
    }

      public void setId(Integer id) {
          this.id = id;
      }
    
    public Integer getComboId() {
        return comboId;
    }

      public void setComboId(Integer comboId) {
          this.comboId = comboId;
      }
    
    public String getTitle() {
        return title;
    }

      public void setTitle(String title) {
          this.title = title;
      }
    
    public Boolean getMapNews() {
        return mapNews;
    }

      public void setMapNews(Boolean mapNews) {
          this.mapNews = mapNews;
      }
    
    public Integer getAreaLevel() {
        return areaLevel;
    }

      public void setAreaLevel(Integer areaLevel) {
          this.areaLevel = areaLevel;
      }
    
    public Integer getAreaId() {
        return areaId;
    }

      public void setAreaId(Integer areaId) {
          this.areaId = areaId;
      }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }

      public void setCreateTime(LocalDateTime createTime) {
          this.createTime = createTime;
      }
    
    public LocalDateTime getNewsTime() {
        return newsTime;
    }

      public void setNewsTime(LocalDateTime newsTime) {
          this.newsTime = newsTime;
      }
    
    public String getLocationSubtitle() {
        return locationSubtitle;
    }

      public void setLocationSubtitle(String locationSubtitle) {
          this.locationSubtitle = locationSubtitle;
      }
    
    public String getVideoUrl() {
        return videoUrl;
    }

      public void setVideoUrl(String videoUrl) {
          this.videoUrl = videoUrl;
      }
    
    public String getThumbImg() {
        return thumbImg;
    }

      public void setThumbImg(String thumbImg) {
          this.thumbImg = thumbImg;
      }
    
    public String getSourceUrl() {
        return sourceUrl;
    }

      public void setSourceUrl(String sourceUrl) {
          this.sourceUrl = sourceUrl;
      }
    
    public String getSourceName() {
        return sourceName;
    }

      public void setSourceName(String sourceName) {
          this.sourceName = sourceName;
      }
    
    public String getNewsContent() {
        return newsContent;
    }

      public void setNewsContent(String newsContent) {
          this.newsContent = newsContent;
      }
    
    public String getImages() {
        return images;
    }

      public void setImages(String images) {
          this.images = images;
      }
    
    public LocalDate getFormattedNewsDate() {
        return formattedNewsDate;
    }

      public void setFormattedNewsDate(LocalDate formattedNewsDate) {
          this.formattedNewsDate = formattedNewsDate;
      }
    
    public Integer getNewsType() {
        return newsType;
    }

      public void setNewsType(Integer newsType) {
          this.newsType = newsType;
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

    @Override
    public String toString() {
        return "TNews{" +
                  "id = " + id +
                      ", comboId = " + comboId +
                      ", title = " + title +
                      ", mapNews = " + mapNews +
                      ", areaLevel = " + areaLevel +
                      ", areaId = " + areaId +
                      ", createTime = " + createTime +
                      ", newsTime = " + newsTime +
                      ", locationSubtitle = " + locationSubtitle +
                      ", videoUrl = " + videoUrl +
                      ", thumbImg = " + thumbImg +
                      ", sourceUrl = " + sourceUrl +
                      ", sourceName = " + sourceName +
                      ", newsContent = " + newsContent +
                      ", images = " + images +
                      ", formattedNewsDate = " + formattedNewsDate +
                      ", newsType = " + newsType +
                      ", lat = " + lat +
                      ", lng = " + lng +
                  "}";
    }
}
