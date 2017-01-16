Exercise instructions:

1.In IntelliJ IDEA, create a Gradle project. Add all required Spark dependencies, and create the directory and package structure of the application. Save all static assets into the proper directory.
----------------------------------------------------------------------------------------------------------------
2.Create model classes for blog entries and blog comments.
----------------------------------------------------------------------------------------------------------------
3.Create a DAO interface for data storage and access and implement it.
----------------------------------------------------------------------------------------------------------------
4.Add necessary routes
----------------------------------------------------------------------------------------------------------------
5.Create index view as the homepage. This view contains a list of blog entries, which displays Title, Date/Time Created. Title should be hyperlinked to the detail page for each blog entry. Include a link to add an entry.
----------------------------------------------------------------------------------------------------------------
6.Create detail page displaying blog entry and submitted comments. Detail page should also display a comment form with Name and Comment. Include a link to edit the entry.
----------------------------------------------------------------------------------------------------------------
7.Create a password page that requires a user to provide 'admin' as the username. This page should display 
before adding or editing a blog entry if there is no cookie present that has the value 'admin'.
----------------------------------------------------------------------------------------------------------------
8.Create add/edit page that is blocked by a password page, using before filter.
----------------------------------------------------------------------------------------------------------------
9.Use CSS to style headings, font colors, blog entry container colors, body colors.

Extra credit:

1.Add tags to blog entries, which enables the ability to categorize. A blog entry can exist with no tags, or have multiple tags.
----------------------------------------------------------------------------------------------------------------
2.Add the ability to delete a blog entry.
----------------------------------------------------------------------------------------------------------------
3.Issue a cookie upon entering the password, and check for it upon adding or editing a post.
