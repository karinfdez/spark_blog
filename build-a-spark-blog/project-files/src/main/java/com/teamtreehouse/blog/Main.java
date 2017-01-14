package com.teamtreehouse.blog;

import com.github.slugify.Slugify;
import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import com.teamtreehouse.blog.model.SimpleBlogDAO;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
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
        HandlebarsTemplateEngine templateEngine=new HandlebarsTemplateEngine();

        //Show a list of blogs
        get("/", (req, res) ->{
            Map<String,Object> model=new HashMap<>();
            model.put("blogLists",blogList.findAllEntries());
            return new ModelAndView(model, "index.hbs");
        }, templateEngine);

        //Save data when creating new blog, redirects to "/" if title and
        //entry are not empty, otherwise, redirects to "/new"
        post("/", (req, res) ->{
            String title=req.queryParams("title");
            String entry=req.queryParams("entry");
            if(!title.isEmpty() && !entry.isEmpty()){
                BlogEntry blog= new BlogEntry(title,entry);
                blogList.addEntry(blog);
                res.redirect("/");
            }else{
                res.redirect("/new");
//                setFlashMessage(req, "One of the fields were empty. Try again!");
            }
            return null;
        });


        get("/new", (req,res) ->{
            return new ModelAndView(null, "new.hbs");
        }, templateEngine);

        //Show specific post using the slug

        //associate the model with the detail.hbs template
        //When user clicks on title, goes to "/detail/:slug"
        //save with the key "detail", the object associated by the slug
        //Now is ready to be used on detail.hbs template.
        get("/detail/:slug", (req, res) ->{
            Map<String,Object> model=new HashMap<>();
            model.put("detail",blogList.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model,"detail.hbs");
        },templateEngine);

        //To save the comments on post
        post ("/detail/:slug", (Request req, Response res) ->{
            BlogEntry entry= blogList.findEntryBySlug(req.params("slug"));
            String name=req.queryParams("name");
            String comment=req.queryParams("comment");
            Comment newComment= new Comment(name,comment);
            entry.addComment(newComment);
//              for(Comment list:entry.getListComments()){
//                  System.out.println(list.getName());
//                  System.out.println(list.getComment());
//              }
            res.redirect("/detail/"+req.params("slug"));
            return null;
        });

        //Edit post
        get("/detail/:slug/edit", (req, res) ->{
            Map<String,Object> model=new HashMap<>();
            model.put("editDetail",blogList.findEntryBySlug(req.params("slug")));
            return new ModelAndView(model,"edit.hbs");
        },templateEngine);

        post ("/detail/:slug/edit", (Request req, Response res) ->{
            Slugify slugify = new Slugify();
            BlogEntry entry= blogList.findEntryBySlug(req.params("slug"));
            String newTitle=req.queryParams("title");
            String newText=req.queryParams("entry");
            String newSlug=slugify.slugify(newTitle);
            entry.setEntry(newText);
            entry.setTitle(newTitle);
            entry.setSlug(newSlug);
            System.out.println(entry.getTitle());
            System.out.println(entry.getEntry());
            res.redirect("/detail/"+entry.getSlug());
            return null;
        });


    }


}

