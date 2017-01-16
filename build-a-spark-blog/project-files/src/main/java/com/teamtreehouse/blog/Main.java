package com.teamtreehouse.blog;

import com.github.slugify.Slugify;
import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import com.teamtreehouse.blog.model.NotFoundException;
import com.teamtreehouse.blog.model.SimpleBlogDAO;
import oracle.jrockit.jfr.StringConstantPool;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        //Makes sure cookie is created and edit/add options can be used
        //several times without entering the password everytime.
        before((req, res) -> {
            if (req.cookie("password") != null) {
                req.attribute("password", req.cookie("password"));
            }
        });

//Creates 3 blogs by default the first time
        before("/",(req, res) -> {
            if (blogList.findAllEntries().isEmpty()) {
                BlogEntry entry1 = new BlogEntry(
                        "The best day I’ve ever had",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, vel tincidunt neque. Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien. Vivamus pharetra massa libero, sed feugiat turpis efficitur at.\n" +
                                "\n" +
                                "Cras egestas ac ipsum in posuere. Fusce suscipit, libero id malesuada placerat, orci velit semper metus, quis pulvinar sem nunc vel augue. In ornare tempor metus, sit amet congue justo porta et. Etiam pretium, sapien non fermentum consequat, +" +
                                "dolor augue gravida lacus, non accumsan lorem odio id risus. Vestibulum pharetra tempor molestie. Integer sollicitudin ante ipsum, a luctus nisi egestas eu. Cras accumsan cursus ante, non dapibus tempor.",
                        "Travel");

                BlogEntry entry2 = new BlogEntry(
                        "Jazz is always welcome",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, vel tincidunt neque. Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien. Vivamus pharetra massa libero, sed feugiat turpis efficitur at.\n" +
                                "\n" +
                                "Cras egestas ac ipsum in posuere. Fusce suscipit, libero id malesuada placerat, orci velit semper metus, quis pulvinar sem nunc vel augue. ",
                        "Music");

                BlogEntry entry3 = new BlogEntry(
                        "What Book I love the most",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc ut rhoncus felis, vel tincidunt neque. Vestibulum ut metus eleifend, malesuada nisl at, scelerisque sapien. Vivamus pharetra massa libero, sed feugiat turpis efficitur at.\n" +
                                "\n" +
                                "Cras egestas ac ipsum in posuere. Fusce suscipit, libero id malesuada placerat, orci velit semper metus, quis pulvinar sem nunc vel augue. ",
                        "Books");

                blogList.addEntry(entry1);
                blogList.addEntry(entry2);
                blogList.addEntry(entry3);
            }
        });

        before("/new",(req, res) -> {
            if (req.attribute("password") == null) {
                setFlashMessage(req,"Please enter password first");
                res.redirect("/sign-in");
                halt();
            }
        });

        before("/detail/:slug/edit",(req, res) -> {
            if (req.attribute("password") == null) {
                setFlashMessage(req,"Please enter password first");
                res.redirect("/sign-in");
                halt();
            }
        });

        //Show a list of blogs
        get("/", (req, res) ->{
            modelIndex.put("blogLists",blogList.findAllEntries());
            modelIndex.put("flashMessage",captureFlashMessage(req));
            modelIndex.put("password", req.attribute("password"));
            modelIndex.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(modelIndex, "index.hbs");
        }, templateEngine);

        //Save data when creating new blog, redirects to "/" if title and
        //entry are not empty, otherwise, redirects to "/new"
        post("/", (req, res) ->{
            String title=req.queryParams("title");
            String entry=req.queryParams("entry");
            String category=req.queryParams("category");
            if(!title.isEmpty() && !entry.isEmpty() && !category.isEmpty()){
                BlogEntry blog= new BlogEntry(title,entry,category);
                blogList.addEntry(blog);
                req.attribute("password");
                res.redirect("/");
            }else{
                setFlashMessage(req, "Field can't be empty. Try again!");
                res.redirect("/new");

            }
            return null;
        });

        post("password", (req,res)->{
            Map<String,String> model=new HashMap<>();
            String password=req.queryParams("password");
            if(password.equals("admin")){
//                Create a cookie based on the password
                res.cookie("password", password);
                res.redirect("/");
            }else{
                setFlashMessage(req, "Invalid password.Try again!");
                res.redirect("/sign-in");
            }

            return null;
        });

        get("sign-in", (req,res)->{
            Map<String,String> model=new HashMap<>();
            model.put("flashMessage",captureFlashMessage(req));
            return new ModelAndView(model, "password.hbs");
        }, templateEngine);


        get("/new", (req,res) ->{
            Map<String,String> model=new HashMap<>();
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "new.hbs");
        }, templateEngine);

        //Show specific post using the slug

        //associate the model with the detail.hbs template
        //When user clicks on title, goes to "/detail/:slug"
        //save with the key "detail", the object associated by the slug
        //Now is ready to be used on detail.hbs template.
        get("/detail/:slug", (req, res) ->{
            Map<String,Object> model=new HashMap<>();
            model.put("detail",blogList.findEntryBySlug(req.params("slug")));
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model,"detail.hbs");
        },templateEngine);

        //To save the comments on post
        post ("/detail/:slug", (Request req, Response res) ->{
            BlogEntry entry= blogList.findEntryBySlug(req.params("slug"));
            String name=req.queryParams("name");
            String comment=req.queryParams("comment");
            if(name.isEmpty()){
                name="Anonymous";
            }
            if(comment.isEmpty()){
                setFlashMessage(req,"The comment section is required");
                res.redirect("/detail/"+entry.getSlug());
            }else{
                Comment newComment= new Comment(name,comment);
                entry.addComment(newComment);
                res.redirect("/detail/"+req.params("slug"));
            }
            return null;
        });

        //Edit post
        get("/detail/:slug/edit", (req, res) ->{
            Map<String,Object> model=new HashMap<>();
            model.put("editDetail",blogList.findEntryBySlug(req.params("slug")));
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model,"edit.hbs");
        },templateEngine);

        post ("/detail/:slug/edit", (Request req, Response res) ->{
            Slugify slugify = new Slugify();
            BlogEntry entry= blogList.findEntryBySlug(req.params("slug"));
            String newTitle=req.queryParams("title");
            String newText=req.queryParams("entry");
            String newCategory=req.queryParams("category");
            if (!newTitle.isEmpty() && !newText.isEmpty() && !newCategory.isEmpty()) {
                String newSlug=slugify.slugify(newTitle);
                entry.setEntry(newText);
                entry.setTitle(newTitle);
                entry.setSlug(newSlug);
                res.redirect("/detail/"+entry.getSlug());
            }else{
                setFlashMessage(req, "These are required fields and can't be empty");
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

