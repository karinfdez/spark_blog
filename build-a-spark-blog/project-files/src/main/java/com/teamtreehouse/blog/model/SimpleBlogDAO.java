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

    @Override
    public BlogEntry findEntryBySlug(String slug) {
        return null;
    }
}

