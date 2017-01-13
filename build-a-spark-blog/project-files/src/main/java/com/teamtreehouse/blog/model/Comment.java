package com.teamtreehouse.blog.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Comment {
    private String name;
    private String comment;
    private String dateFormat;
    private LocalDateTime now;

    public Comment(String name, String comment) {
        this.name = name;
        this.comment = comment;
        now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int minute = now.getMinute();
        this.dateFormat = now.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm", Locale.ENGLISH));

    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }

    public String getDateFormat() {
        return dateFormat;
    }
}
