package com.teamtreehouse.blog;

import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.SimpleBlogDAO;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static spark.Spark.*;

/**
 * Created by karinfernandez on 1/8/17.
 */
public class Main {
    public static void main(String[] args) {

        staticFileLocation("/public");
        BlogDao blogList = new SimpleBlogDAO();


        get("/", (req, res) ->{
            return new ModelAndView(null, "index.hbs");
        }, new HandlebarsTemplateEngine());


        get("/new", (req,res) ->{
            return new ModelAndView(null, "new.hbs");
        }, new HandlebarsTemplateEngine());

        //Save data when creating new blog, redirects to detail.hbs
        post("/detail", (req, res) ->{
            String title=req.queryParams("title");
            String entry=req.queryParams("entry");
            BlogEntry blog= new BlogEntry(title,entry);
            blogList.addEntry(blog);
            res.redirect("/detail");
            return null;
        });
         //Show a specific blog
        get("/detail", (req, res) ->{
            Map<String,Object> model=new HashMap<>();
            model.put("detail",blogList.findAllEntries());
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());
    }
}

