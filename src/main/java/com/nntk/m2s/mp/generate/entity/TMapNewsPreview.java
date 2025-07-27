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
 * @since 2025-07-27
 */
@TableName("t_map_news_preview")
public class TMapNewsPreview implements Serializable {

        private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private LocalDateTime createTime;

    private String title;

    private Integer coverId;
    
    public Integer getId() {
        return id;
    }

      public void setId(Integer id) {
          this.id = id;
      }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }

      public void setCreateTime(LocalDateTime createTime) {
          this.createTime = createTime;
      }
    
    public String getTitle() {
        return title;
    }

      public void setTitle(String title) {
          this.title = title;
      }
    
    public Integer getCoverId() {
        return coverId;
    }

      public void setCoverId(Integer coverId) {
          this.coverId = coverId;
      }

    @Override
    public String toString() {
        return "TMapNewsPreview{" +
                  "id = " + id +
                      ", createTime = " + createTime +
                      ", title = " + title +
                      ", coverId = " + coverId +
                  "}";
    }
}
