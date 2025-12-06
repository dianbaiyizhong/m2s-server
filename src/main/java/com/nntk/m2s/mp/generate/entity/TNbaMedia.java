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
 * @since 2025-12-06
 */
@TableName("t_nba_media")
public class TNbaMedia implements Serializable {

        private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String title;

    private String previewUrl;

    private String src;

    private Integer type;
    
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
    
    public String getPreviewUrl() {
        return previewUrl;
    }

      public void setPreviewUrl(String previewUrl) {
          this.previewUrl = previewUrl;
      }
    
    public String getSrc() {
        return src;
    }

      public void setSrc(String src) {
          this.src = src;
      }
    
    public Integer getType() {
        return type;
    }

      public void setType(Integer type) {
          this.type = type;
      }

    @Override
    public String toString() {
        return "TNbaMedia{" +
                  "id = " + id +
                      ", title = " + title +
                      ", previewUrl = " + previewUrl +
                      ", src = " + src +
                      ", type = " + type +
                  "}";
    }
}
