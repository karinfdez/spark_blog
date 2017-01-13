package com.teamtreehouse.blog.model;

import com.github.slugify.Slugify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BlogEntry {

    private String title;
    private String entry;
    private LocalDateTime now;
    private String slug;
    private String dateFormat;
    private List<Comment> listComments;


    public BlogEntry(String title,String entry) {
        this.title = title;
        this.entry=entry;
        now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        int hour = now.getHour();
        int minute = now.getMinute();
        Slugify slg = new Slugify();
        this.slug=slg.slugify(title);
        this.dateFormat = now.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm", Locale.ENGLISH));
        listComments=new ArrayList<>();
    }

    public boolean addComment(Comment comment) {
        return listComments.add(comment);
    }

    public String getDate() {
        return dateFormat;
    }

    public List<Comment> getListComments() {
        return new ArrayList<>(listComments);
    }

    public String getTitle() {
        return title;
    }

    public String getEntry() {
        return entry;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlogEntry blogEntry = (BlogEntry) o;

        if (title != null ? !title.equals(blogEntry.title) : blogEntry.title != null) return false;
        if (entry != null ? !entry.equals(blogEntry.entry) : blogEntry.entry != null) return false;
        if (now != null ? !now.equals(blogEntry.now) : blogEntry.now != null) return false;
        if (slug != null ? !slug.equals(blogEntry.slug) : blogEntry.slug != null) return false;
        return dateFormat != null ? dateFormat.equals(blogEntry.dateFormat) : blogEntry.dateFormat == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (entry != null ? entry.hashCode() : 0);
        result = 31 * result + (now != null ? now.hashCode() : 0);
        result = 31 * result + (slug != null ? slug.hashCode() : 0);
        result = 31 * result + (dateFormat != null ? dateFormat.hashCode() : 0);
        return result;
    }

    public String getSlug() {

        return slug;
    }

    @Override
    public String toString() {
        return "BlogEntry{" +
                "title='" + title + '\'' +
                "date: " + dateFormat.toString()+
                '}';
    }
}
