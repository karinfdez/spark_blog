package com.teamtreehouse.blog;

import com.github.slugify.Slugify;
import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import com.teamtreehouse.blog.model.NotFoundException;
import com.teamtreehouse.blog.model.SimpleBlogDAO;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/**
 * Created by karinfernandez on 1/8/17.
 */
public class Main {
    private static final String FLASH_MESSAGE_KEY = "flash_message";
    public static void main(String[] args) {

        staticFileLocation("/public");
        BlogDao blogList = new SimpleBlogDAO();
        HandlebarsTemplateEngine templateEngine=new HandlebarsTemplateEngine();
        Map<String,Object> modelIndex=new HashMap<>();

        //Show a list of blogs
        get("/", (req, res) ->{
            modelIndex.put("blogLists",blogList.findAllEntries());
            modelIndex.put("flashMessage",captureFlashMessage(req));
            return new ModelAndView(modelIndex, "index.hbs");
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
            if (!newTitle.isEmpty() && !newText.isEmpty()) {
                String newSlug=slugify.slugify(newTitle);
                entry.setEntry(newText);
                entry.setTitle(newTitle);
                entry.setSlug(newSlug);
                res.redirect("/detail/"+entry.getSlug());
            }else{
                res.redirect("/detail/"+entry.getSlug()+"/edit");
            }
            return null;
        });

        //Delete blog entry
        post("/detail/:slug/delete", (req, res) -> {
            String slug=req.params("slug");
            BlogEntry entry= blogList.findEntryBySlug(slug);
            if (blogList.removeBlog(entry)){
                setFlashMessage(req, "Blog "+entry.getTitle()+"was removed successfully");
            }else{
                setFlashMessage(req, "Blog couldn't be removed");
            }
            res.redirect("/");
            return null;
        });

        exception(NotFoundException.class,(exc, req,res) ->{
            res.status(404);
            String html=templateEngine.render(new ModelAndView(null,"not-found.hbs"));
            res.body(html);
        });

//        post("/sign-in", (req,res) -> {
//
//        });


    }

    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    private static String getFlashMessage(Request req) {
        if (req.session(false) == null) {
            return null;
        }
        if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)) {
            return null;
        }
        return (String) req.session().attribute(FLASH_MESSAGE_KEY);
    }

    private static String captureFlashMessage(Request req) {
        String message = getFlashMessage(req);
        if (message != null) {
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }
}

