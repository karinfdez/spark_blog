package com.teamtreehouse.blog.model;

import java.util.Date;

public class BlogEntry {

    private String title;
    private String entry;
    private Date date;


    public BlogEntry(String title,String entry) {
        this.title = title;
        this.entry=entry;
        this.date=new Date();

    }

    public boolean addComment(Comment comment) {
        // Store these comments!
        return false;
    }

    public String getTitle() {
        return title;
    }

    public String getEntry() {
        return entry;
    }

    @Override
    public String toString() {
        return "BlogEntry{" +
                "title='" + title + '\'' +
                "date: " + date.toString()+
                '}';
    }
}
