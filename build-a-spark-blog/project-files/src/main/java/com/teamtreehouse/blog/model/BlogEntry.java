package com.teamtreehouse.blog.model;

import java.util.Date;

public class BlogEntry {

    private String title;
    private Date date;


    public BlogEntry(String title) {
        this.title = title;
        date=new Date();

    }

    public boolean addComment(Comment comment) {
        // Store these comments!
        return false;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "BlogEntry{" +
                "title='" + title + '\'' +
                "date: " + date.toString()+
                '}';
    }
}
