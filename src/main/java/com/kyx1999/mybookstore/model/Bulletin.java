package com.kyx1999.mybookstore.model;

import java.util.Date;

public class Bulletin {
    private String content;

    private Date date;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
