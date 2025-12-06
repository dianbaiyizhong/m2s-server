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
 * @since 2025-12-06
 */
@TableName("t_cctv_task")
public class TCctvTask implements Serializable {

        private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String url;

    private Integer status;

    private Integer comboId;

    private LocalDateTime createTime;

    private String cctvTime;
    
    public Integer getId() {
        return id;
    }

      public void setId(Integer id) {
          this.id = id;
      }
    
    public String getUrl() {
        return url;
    }

      public void setUrl(String url) {
          this.url = url;
      }
    
    public Integer getStatus() {
        return status;
    }

      public void setStatus(Integer status) {
          this.status = status;
      }
    
    public Integer getComboId() {
        return comboId;
    }

      public void setComboId(Integer comboId) {
          this.comboId = comboId;
      }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }

      public void setCreateTime(LocalDateTime createTime) {
          this.createTime = createTime;
      }
    
    public String getCctvTime() {
        return cctvTime;
    }

      public void setCctvTime(String cctvTime) {
          this.cctvTime = cctvTime;
      }

    @Override
    public String toString() {
        return "TCctvTask{" +
                  "id = " + id +
                      ", url = " + url +
                      ", status = " + status +
                      ", comboId = " + comboId +
                      ", createTime = " + createTime +
                      ", cctvTime = " + cctvTime +
                  "}";
    }
}
