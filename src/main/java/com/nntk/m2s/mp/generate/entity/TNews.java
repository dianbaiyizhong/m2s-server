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
 * @since 2025-07-16
 */
@TableName("t_news")
public class TNews implements Serializable {

        private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String title;

    private Integer areaLevel;

    private LocalDateTime createTime;

    private Integer areaId;

    private LocalDateTime newsTime;

    private String thumbImg;

    private String sourceUrl;

    private String sourceName;

    private String newsContext;

    private Boolean mapNews;

    private String videoUrl;

    private Integer comboId;

    private String locationSubtitle;

    private String images;
    
    public Integer getId() {
        return id;
    }

      public void setId(Integer id) {
          this.id = id;
      }
    
    public String getTitle() {
        return title;
    }

      public void setTitle(String title) {
          this.title = title;
      }
    
    public Integer getAreaLevel() {
        return areaLevel;
    }

      public void setAreaLevel(Integer areaLevel) {
          this.areaLevel = areaLevel;
      }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }

      public void setCreateTime(LocalDateTime createTime) {
          this.createTime = createTime;
      }
    
    public Integer getAreaId() {
        return areaId;
    }

      public void setAreaId(Integer areaId) {
          this.areaId = areaId;
      }
    
    public LocalDateTime getNewsTime() {
        return newsTime;
    }

      public void setNewsTime(LocalDateTime newsTime) {
          this.newsTime = newsTime;
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
    
    public String getNewsContext() {
        return newsContext;
    }

      public void setNewsContext(String newsContext) {
          this.newsContext = newsContext;
      }
    
    public Boolean getMapNews() {
        return mapNews;
    }

      public void setMapNews(Boolean mapNews) {
          this.mapNews = mapNews;
      }
    
    public String getVideoUrl() {
        return videoUrl;
    }

      public void setVideoUrl(String videoUrl) {
          this.videoUrl = videoUrl;
      }
    
    public Integer getComboId() {
        return comboId;
    }

      public void setComboId(Integer comboId) {
          this.comboId = comboId;
      }
    
    public String getLocationSubtitle() {
        return locationSubtitle;
    }

      public void setLocationSubtitle(String locationSubtitle) {
          this.locationSubtitle = locationSubtitle;
      }
    
    public String getImages() {
        return images;
    }

      public void setImages(String images) {
          this.images = images;
      }

    @Override
    public String toString() {
        return "TNews{" +
                  "id = " + id +
                      ", title = " + title +
                      ", areaLevel = " + areaLevel +
                      ", createTime = " + createTime +
                      ", areaId = " + areaId +
                      ", newsTime = " + newsTime +
                      ", thumbImg = " + thumbImg +
                      ", sourceUrl = " + sourceUrl +
                      ", sourceName = " + sourceName +
                      ", newsContext = " + newsContext +
                      ", mapNews = " + mapNews +
                      ", videoUrl = " + videoUrl +
                      ", comboId = " + comboId +
                      ", locationSubtitle = " + locationSubtitle +
                      ", images = " + images +
                  "}";
    }
}
