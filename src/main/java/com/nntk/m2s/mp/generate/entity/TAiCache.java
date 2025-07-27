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
@TableName("t_ai_cache")
public class TAiCache implements Serializable {

        private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String prompt;

    private String content;

    private String promptMd5;

    private LocalDateTime createTime;
    
    public Integer getId() {
        return id;
    }

      public void setId(Integer id) {
          this.id = id;
      }
    
    public String getPrompt() {
        return prompt;
    }

      public void setPrompt(String prompt) {
          this.prompt = prompt;
      }
    
    public String getContent() {
        return content;
    }

      public void setContent(String content) {
          this.content = content;
      }
    
    public String getPromptMd5() {
        return promptMd5;
    }

      public void setPromptMd5(String promptMd5) {
          this.promptMd5 = promptMd5;
      }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }

      public void setCreateTime(LocalDateTime createTime) {
          this.createTime = createTime;
      }

    @Override
    public String toString() {
        return "TAiCache{" +
                  "id = " + id +
                      ", prompt = " + prompt +
                      ", content = " + content +
                      ", promptMd5 = " + promptMd5 +
                      ", createTime = " + createTime +
                  "}";
    }
}
