package com.teamtreehouse.blog.model;


import com.teamtreehouse.blog.dao.BlogDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karinfernandez on 1/9/17.
 */
public class SimpleBlogDAO implements BlogDao {

    private List <BlogEntry> blogList;

    public SimpleBlogDAO() {
        blogList=new ArrayList<>();
    }

    @Override
    public boolean addEntry(BlogEntry blogEntry) {
        if (blogList.add(blogEntry)) return true;
        else return false;
    }

    @Override
    public List<BlogEntry> findAllEntries() {
        return new ArrayList<>(blogList);
    }

    //Try to find a slug, and if it can't find it
    //it will throw a new exception: NotFoundException.
    @Override
    public BlogEntry findEntryBySlug(String slug) {
        return blogList.stream()
                        .filter(blogEntry -> blogEntry.getSlug().equals(slug))
                        .findFirst()
                        .orElseThrow(NotFoundException::new);
    }

    public boolean removeBlog(BlogEntry entry) {
        return blogList.remove (entry);
    }
}

