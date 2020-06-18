package com.kyx1999.mybookstore.model;

import java.util.Date;

public class Bulletin {
    private Integer bltid;

    private String content;

    private Date time;

    private Boolean valid;

    public Integer getBltid() {
        return bltid;
    }

    public void setBltid(Integer bltid) {
        this.bltid = bltid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
