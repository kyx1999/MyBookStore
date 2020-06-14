package com.kyx1999.mybookstore.model;

import java.util.Date;

public class Bulletin {
    private String content;

    private Date time;

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
}
