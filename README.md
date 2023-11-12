# WEare social network
Teodora Ivanova, Stanimir Kolev

*This document describes a sample team project assignment for the Java cohort at Telerik Academy.*  
  
**Project Description**
  
The task is to develop a SOCIAL NETWORK web application. The SOCIAL NETWORK application
enables users to: connect with people; create, comment and like posts; get a feed of the newest/most relevant posts of your connections.
This is a social network for exchanging of services allowing the users to set their profession/skills and make posts with the services they offer (e.g. online lawyer consultations). 

## **Functional Requirements**
  
***General***
  
Public Part (anonymous users)
The public part of the projects is visible without authentication. This includes:
Application home page – a landing page with links to the login and registration forms,
profile search and public feed
- Login form
- Registration form
- Public profiles – shows the profile information, that the user decided to be public
(profile picture etc.) and his/hers public posts ordered chronologically
- Search profiles – search for profiles based on name or professional category
- Public feed – chronologically ordered public posts. More details about the posts follow
in the next sections.
  
Private Part (registered users)
Registered users have private part in the web application accessible after successful
login.
Registered users have a profile administration page where they can edit their personal and
login information. This includes:
Change name
- Upload a profile picture
- Set visibility of picture (public – visible for everyone including non-registered users,
connections only – visible only for connected profiles)
- Change email
- Change/Add gender
- Change/Add location
- Change/Add personal review 
- Change/Add proffessional category
- Change/Add skills

Registered users also have a professional profile where they can describe their services in details.

The users can request to connect, or to disconnect each other (depending on the current status).

Registered users can create posts. The user can choose for it to be either public (visible for everyone, including nonregistered
users) or connections only. 
Post contains text, publicity and a picture.
Registered users have a personalized post feed, where they can see a feed formed from their connection’s posts.
Complex algorithm for feed generation is using 5 criteria:
 date of the post, likes, comments, if the crietor is a connection and number of post per month
- the feed is generated chronologically
- the number of interactions with the post(comments, likes) place posts higher in the feed
- the number of posts per month of one user is included, where every next post is pushed down the feed to prevent spam
- if the post'creator is a connection to ne user viewering the post will be placed higher in the feed

Registered users have interactions with other user’s posts. They can:
- Like a post – Registered users can like or dislike(if you have already liked it) a post. The
post total like count is showed along the post content.
- Like/Dislike a comment - The comment like count in the feed generation algorithm
- Comment a post – Under each post there is a comment section. Registered users can
comment on a post. The newest n comments are shown along the post content. Users can expand the
comment section of the post in order to read older comments.

Administration Part
System administrators have administrative access to the system and permissions to
administer all major information objects in the system. They should be able to:
- Edit and Disable/Enable profiles
- Edit/Delete post
- Edit/Delete comments

## **REST API **
   
  
**REST API Specification:**  
  
 - Posts
  	- CRUD Operations  
  
 - Comments
  	- CRUD Operations  
  
 - Request and approve for connection
      
 - Users
  	- GET, UPDATE, CREATE Operaitons


   
Technical details


Database
The data of the application is stored in a relational database – MySQL/MariaDB.

Backend
- JDK version 1.8
- SpringMVC and SpringBoot framework
- Hibernate/JPA repository in the persistence (repository) layer
- Spring Security to handle user registration and user roles
- Service layer have 88% unit test coverage

Frontend
- Spring MVC Framework with Thymeleaf template engine for generating the UI
- AJAX for making asynchronous requests to the server 
- Bootstrap, HTML & CSS 


