package com.community.weare;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.*;
import com.community.weare.Models.dto.PostDTO;
import com.community.weare.Repositories.PostRepository;
import com.community.weare.Services.contents.CommentService;
import com.community.weare.Services.contents.PostServiceImpl;
import com.community.weare.Services.users.UserServiceImpl;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PostServiceImplTests {

    @InjectMocks
    PostServiceImpl mockPostService;

    @Mock
    UserServiceImpl userService;
    @Mock
    PostRepository postRepository;
    @Mock
    CommentService commentService;

    @Test
    public void findAllShould_CallRepository() {
        //arrange
        Post post = FactoryPostComment.createPost();
        List<Post> list = new ArrayList<>();
        list.add(post);

        Mockito.when(postRepository.findAll()).thenReturn(list);
        //act
        mockPostService.findAll();
        //assert
        Mockito.verify(postRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void findAllShould_SortPostsByDate() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        post1.setDate("1");
        Post post2 = FactoryPostComment.createPost();
        post2.setDate("2");
        Post post3 = FactoryPostComment.createPost();
        post3.setDate("3");

        List<Post> list = new ArrayList<>();
        list.add(post2);
        list.add(post1);
        list.add(post3);

        List<Post> listSorted = new ArrayList<>();
        listSorted.add(post3);
        listSorted.add(post2);
        listSorted.add(post1);

        Mockito.when(postRepository.findAll(Sort.by(Sort.Direction.DESC, "postId"))).thenReturn(listSorted);
        //act
        List<Post> listResult = mockPostService.findAll(Sort.by(Sort.Direction.DESC, "postId"));
        //assert
        assertEquals(list.get(0), listResult.get(1));
        assertEquals(list.get(1), listResult.get(2));
        assertEquals(list.get(2), listResult.get(0));
    }

    @Test
    public void findAllByUserShould_ReturnWhenPrincipalIsNull() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        post1.setDate("20/04/2020 11:34:13");
        Post post2 = FactoryPostComment.createPost();
        post2.setDate("20/04/2020 12:34:13");
        Post post3 = FactoryPostComment.createPost();
        post3.setDate("20/04/2020 13:34:13");
        post3.setPublic(false);
        Post post4 = FactoryPostComment.createPost();
        post4.setDate("20/04/2020 14:34:13");
        post4.setPublic(false);

        User user1 = Factory.createUser();
        user1.setUsername("TheChosenOne");
        post1.setUser(user1);
        post2.setUser(user1);
        post3.setUser(user1);
        post4.setUser(user1);

//        Principal principal = () -> "tedi";

        List<Post> allUserPosts = new ArrayList<>();
        allUserPosts.add(post4);
        allUserPosts.add(post3);
        allUserPosts.add(post2);
        allUserPosts.add(post1);

        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"), "TheChosenOne")).thenReturn(allUserPosts);
        //act
        List<Post> listResult = mockPostService.findAllByUser("TheChosenOne", null);

        //assert
        assertEquals(2, listResult.size());
        assertEquals(post2, listResult.get(0));
        assertEquals(post1, listResult.get(1));
    }

    @Test
    public void findAllByUserShould_ReturnWhenPrincipalIsFriend() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        post1.setDate("20/04/2020 11:34:13");
        Post post2 = FactoryPostComment.createPost();
        post2.setDate("20/04/2020 12:34:13");
        Post post3 = FactoryPostComment.createPost();
        post3.setDate("20/04/2020 13:34:13");
        post3.setPublic(false);
        Post post4 = FactoryPostComment.createPost();
        post4.setDate("20/04/2020 14:34:13");
        post4.setPublic(false);

        User user1 = Factory.createUser();
        user1.setUsername("TheChosenOne");
        post1.setUser(user1);
        post2.setUser(user1);
        post3.setUser(user1);
        post4.setUser(user1);

        User userTedi = Factory.createUser();
        Principal principal = () -> "tedi";

        user1.getFriendList().add(userTedi);
        userTedi.getFriendList().add(user1);

        List<Post> allUserPosts = new ArrayList<>();
        allUserPosts.add(post4);
        allUserPosts.add(post3);
        allUserPosts.add(post2);
        allUserPosts.add(post1);

        Mockito.when(userService.getUserByUserName(principal.getName())).thenReturn(userTedi);
        Mockito.when(userService.getUserByUserName(user1.getUsername())).thenReturn(user1);

        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"), "TheChosenOne")).thenReturn(allUserPosts);
        //act
        List<Post> listResult = mockPostService.findAllByUser("TheChosenOne", principal);

        //assert
        assertEquals(4, listResult.size());
        assertEquals(post4, listResult.get(0));
        assertEquals(post3, listResult.get(1));
        assertEquals(post2, listResult.get(2));
        assertEquals(post1, listResult.get(3));
    }

    @Test
    public void findAllByUserShould_ReturnWhenPrincipalIsNotFriend() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        post1.setDate("20/04/2020 11:34:13");
        Post post2 = FactoryPostComment.createPost();
        post2.setDate("20/04/2020 12:34:13");
        Post post3 = FactoryPostComment.createPost();
        post3.setDate("20/04/2020 13:34:13");
        post3.setPublic(false);
        Post post4 = FactoryPostComment.createPost();
        post4.setDate("20/04/2020 14:34:13");
        post4.setPublic(false);

        User user1 = Factory.createUser();
        user1.setUsername("TheChosenOne");
        post1.setUser(user1);
        post2.setUser(user1);
        post3.setUser(user1);
        post4.setUser(user1);

        User userTedi = Factory.createUser();
        Principal principal = () -> "tedi";

        List<Post> allUserPosts = new ArrayList<>();
        allUserPosts.add(post4);
        allUserPosts.add(post3);
        allUserPosts.add(post2);
        allUserPosts.add(post1);

        Mockito.when(userService.getUserByUserName(principal.getName())).thenReturn(userTedi);
        Mockito.when(userService.getUserByUserName(user1.getUsername())).thenReturn(user1);

        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"), "TheChosenOne")).thenReturn(allUserPosts);
        //act
        List<Post> listResult = mockPostService.findAllByUser("TheChosenOne", principal);

        //assert
        assertEquals(2, listResult.size());
        assertEquals(post2, listResult.get(0));
        assertEquals(post1, listResult.get(1));
    }

    @Test
    public void findSliceWithPostsShould_CallRepository() {
        //arrange
        Post post = FactoryPostComment.createPost();
        List<Post> list = new ArrayList<>();
        list.add(post);
        Slice<Post> slice = new PageImpl<>(list);
        int startIndex = 1;
        int pageSize = 1;
        String sortParam = "param1";
        User user = Factory.createUser();
        String username = "userName";
        Pageable page = PageRequest.of(startIndex, pageSize, Sort.by(sortParam).descending());
        Principal principal = () -> "tedi";

        //act
        mockPostService.findSliceWithPosts(startIndex, pageSize, sortParam, user, "tedi");

        //assert
        Mockito.verify(postRepository, Mockito.times(1))
                .findAllByUserUsername(page, "tedi");
    }

    @Test
    public void findSliceWithPostsShould_ThrowIfPageAndIndexAreZero() {
        //arrange
        int startIndex = 0;
        int pageSize = 0;
        String sortParam = "param1";
        String username = "param2";
        User user = Factory.createUser();
        Principal principal = () -> "tedi";

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.findSliceWithPosts(startIndex, pageSize, sortParam, user,"tedi"));
    }

    @Test
    @Ignore
    public void sortingAlgorithmShould_SortCorrectly() {
        //arrange
        User user01 = Factory.createUser();
        user01.setUsername("otherName");
        User userTedi = Factory.createUser();
        userTedi.getFriendList().add(user01);

        Post post1 = FactoryPostComment.createPost();
        post1.setDate("18/04/2020 23:34:13");
        post1.setUser(userTedi);
        post1.getLikes().add(Factory.createUser());
        post1.getLikes().add(Factory.createUser());
        post1.getLikes().add(Factory.createUser());

        post1.getComments().add(new Comment());
        post1.getComments().add(new Comment());

        Post post2 = FactoryPostComment.createPost();
        post2.setDate("19/04/2020 23:34:13");
        post2.setUser(userTedi);

        post2.getLikes().add(Factory.createUser());
        post2.getLikes().add(Factory.createUser());
        post2.getLikes().add(Factory.createUser());
        post2.getLikes().add(Factory.createUser());
        post2.getLikes().add(Factory.createUser());

        post2.getComments().add(new Comment());
        post2.getComments().add(new Comment());
        post2.getComments().add(new Comment());

        Post post3 = FactoryPostComment.createPost();
        post3.setUser(userTedi);
        post3.setDate("20/04/2020 23:34:13");

        List<Post> sortedList = new ArrayList<>();
        sortedList.add(post3);
        sortedList.add(post2);
        sortedList.add(post1);

        Principal principal = () -> "tedi";
        Mockito.when(postRepository.findAll(Sort.by(Sort.Direction.DESC, "postId")))
                .thenReturn(sortedList);

        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post1.getUser().getUsername())).thenReturn(sortedList);
        Mockito.when(userService.getUserByUserName(principal.getName())).thenReturn(userTedi);

        //act
        List<Post> listResult = mockPostService.findAllPostsByAlgorithm
                ((Sort.by(Sort.Direction.DESC, "postId")), principal);

        //assert
        assertEquals(-3, listResult.get(0).getRank(), 0);
        assertEquals(0, listResult.get(1).getRank(), 0);
        assertEquals(13, listResult.get(2).getRank(), 0);
    }

    @Test
    @Ignore
    public void mergeTwoListsShould_applySortingAlgorithm() {
        //arrange
        User user01 = Factory.createUser();
        user01.setUsername("otherName");
        User userTedi = Factory.createUser();
        userTedi.getFriendList().add(user01);
        user01.getFriendList().add(userTedi);

        Post post1 = FactoryPostComment.createPost();
        post1.setDate("18/04/2020 23:34:13");
        post1.setUser(userTedi);
        post1.getLikes().add(Factory.createUser());
        post1.getLikes().add(Factory.createUser());
        post1.getLikes().add(Factory.createUser());

        post1.getComments().add(new Comment());
        post1.getComments().add(new Comment());

        Post post2 = FactoryPostComment.createPost();
        post2.setDate("19/04/2020 23:34:13");
        //the difference
        post2.setUser(user01);

        post2.getLikes().add(Factory.createUser());
        post2.getLikes().add(Factory.createUser());
        post2.getLikes().add(Factory.createUser());
        post2.getLikes().add(Factory.createUser());
        post2.getLikes().add(Factory.createUser());

        post2.getComments().add(new Comment());
        post2.getComments().add(new Comment());
        post2.getComments().add(new Comment());

        Post post3 = FactoryPostComment.createPost();
        post3.setUser(userTedi);
        post3.setDate("20/04/2020 23:34:13");

        List<Post> list1 = new ArrayList<>();
        List<Post> list2 = new ArrayList<>();
        list1.add(post3);
        list1.add(post1);
        list2.add(post2);

        Principal principal = () -> "tedi";

        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post1.getUser().getUsername())).thenReturn(list1);
        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post3.getUser().getUsername())).thenReturn(list1);
        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post2.getUser().getUsername())).thenReturn(list2);
        Mockito.when(userService.getUserByUserName(principal.getName())).thenReturn(userTedi);

        //act
        List<Post> listResult = mockPostService.mergeTwoLists
                (list1, list2, principal);

        //assert
        assertEquals(-23, listResult.get(0).getRank(), 0);
        assertEquals(0, listResult.get(1).getRank(), 0);
        assertEquals(3, listResult.get(2).getRank(), 0);
    }


    @Test
    public void filterPostsByFriendsShould_ReturnFilteredPosts() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        Post post2 = FactoryPostComment.createPost();
        post2.setPublic(false);
        Post post3 = FactoryPostComment.createPost();
        User userTedi = Factory.createUser();
        User user1 = Factory.createUser();
        user1.setUsername("tediFriend");
        userTedi.getFriendList().add(user1);
        user1.getFriendList().add(userTedi);

        post1.setUser(userTedi);
        post2.setUser(userTedi);
        post3.setUser(user1);

        List<Post> list = new ArrayList<>();
        list.add(post1);
        list.add(post2);
        list.add(post3);

        //act
        List<Post> listResult = mockPostService.filterPostsByFriends(list, userTedi);

        //assert
        assertEquals(1, listResult.size());
        assertEquals(post3, listResult.get(0));
    }

    @Test
    @Ignore
    public void findPostsPersonalFeedShould_ReturnFilteredPosts() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        post1.setDate("20/04/2020 11:34:13");
        Post post2 = FactoryPostComment.createPost();
        post2.setDate("20/04/2020 12:34:13");
        Post post3 = FactoryPostComment.createPost();
        post3.setDate("20/04/2020 13:34:13");
        Post post4 = FactoryPostComment.createPost();
        post4.setDate("20/04/2020 14:34:13");
        Post post5 = FactoryPostComment.createPost();
        post5.setDate("20/04/2020 15:34:13");
        Post post6 = FactoryPostComment.createPost();
        post6.setDate("20/04/2020 16:34:13");
        Post post7 = FactoryPostComment.createPost();
        post7.setDate("20/04/2020 17:34:13");

        Principal principal = () -> "tedi";
        User userTedi = Factory.createUser();
        User user1 = Factory.createUser();
        user1.setUsername("tediFriend");
        userTedi.getFriendList().add(user1);
        user1.getFriendList().add(userTedi);
        Category categoryDoctor = new Category("Doctor");
        Category categoryDentist = new Category("Dentist");
        userTedi.getExpertiseProfile().setCategory(categoryDoctor);
        user1.getExpertiseProfile().setCategory(categoryDentist);

        post1.setUser(userTedi);
        post2.setUser(userTedi);
        post3.setUser(user1);

        User userGeorge = Factory.createUser();
        userGeorge.setUsername("George");
        userGeorge.getExpertiseProfile().setCategory(categoryDentist);
        post4.setUser(userGeorge);
        post5.setUser(userGeorge);

        User userAlex = Factory.createUser();
        userAlex.setUsername("Alex");
        userAlex.getExpertiseProfile().setCategory(categoryDoctor);
        post6.setUser(userAlex);
        post7.setUser(userAlex);

        List<Post> listAll = new ArrayList<>();
        listAll.add(post1);
        listAll.add(post2);
        listAll.add(post3);
        listAll.add(post4);
        listAll.add(post5);
        listAll.add(post6);
        listAll.add(post7);

        List<Post> listTedi = new ArrayList<>();
        List<Post> listUser1 = new ArrayList<>();
        List<Post> listGeorge = new ArrayList<>();
        List<Post> listAlex = new ArrayList<>();

        //order is extremely important
        listTedi.add(post2);
        listTedi.add(post1);
        listUser1.add(post3);
        listGeorge.add(post5);
        listGeorge.add(post4);
        listAlex.add(post7);
        listAlex.add(post6);


        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post1.getUser().getUsername())).thenReturn(listTedi);
        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post2.getUser().getUsername())).thenReturn(listTedi);
        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post3.getUser().getUsername())).thenReturn(listUser1);
        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post6.getUser().getUsername())).thenReturn(listAlex);
        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post7.getUser().getUsername())).thenReturn(listAlex);

        Mockito.when(userService.getUserByUserName(principal.getName())).thenReturn(userTedi);
        Mockito.when(postRepository.findAll()).thenReturn(listAll);

        //act
        List<Post> listResult = mockPostService.findPostsPersonalFeed(principal);

        //assert
        assertEquals(5, listResult.size());
        assertEquals(post3, listResult.get(0));
        assertEquals(post7, listResult.get(1));
        assertEquals(post2, listResult.get(2));
        assertEquals(post6, listResult.get(3));
        assertEquals(post1, listResult.get(4));
    }

    @Test
    @Ignore
    public void findPostsByAuthorityShould_ReturnPersonalFeedIfPrincipalNotNull() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        post1.setDate("20/04/2020 11:34:13");
        Post post2 = FactoryPostComment.createPost();
        post2.setDate("20/04/2020 12:34:13");
        Post post3 = FactoryPostComment.createPost();
        post3.setDate("20/04/2020 13:34:13");
        Post post4 = FactoryPostComment.createPost();
        post4.setDate("20/04/2020 14:34:13");
        Post post5 = FactoryPostComment.createPost();
        post5.setDate("20/04/2020 15:34:13");
        Post post6 = FactoryPostComment.createPost();
        post6.setDate("20/04/2020 16:34:13");
        Post post7 = FactoryPostComment.createPost();
        post7.setDate("20/04/2020 17:34:13");

        Principal principal = () -> "tedi";
        User userTedi = Factory.createUser();
        User user1 = Factory.createUser();
        user1.setUsername("tediFriend");
        userTedi.getFriendList().add(user1);
        user1.getFriendList().add(userTedi);
        Category categoryDoctor = new Category("Doctor");
        Category categoryDentist = new Category("Dentist");
        userTedi.getExpertiseProfile().setCategory(categoryDoctor);
        user1.getExpertiseProfile().setCategory(categoryDentist);

        post1.setUser(userTedi);
        post2.setUser(userTedi);
        post3.setUser(user1);

        User userGeorge = Factory.createUser();
        userGeorge.setUsername("George");
        userGeorge.getExpertiseProfile().setCategory(categoryDentist);
        post4.setUser(userGeorge);
        post5.setUser(userGeorge);

        User userAlex = Factory.createUser();
        userAlex.setUsername("Alex");
        userAlex.getExpertiseProfile().setCategory(categoryDoctor);
        post6.setUser(userAlex);
        post7.setUser(userAlex);

        List<Post> listAll = new ArrayList<>();
        listAll.add(post1);
        listAll.add(post2);
        listAll.add(post3);
        listAll.add(post4);
        listAll.add(post5);
        listAll.add(post6);
        listAll.add(post7);

        List<Post> listTedi = new ArrayList<>();
        List<Post> listUser1 = new ArrayList<>();
        List<Post> listGeorge = new ArrayList<>();
        List<Post> listAlex = new ArrayList<>();

        //order is extremely important
        listTedi.add(post2);
        listTedi.add(post1);
        listUser1.add(post3);
        listGeorge.add(post5);
        listGeorge.add(post4);
        listAlex.add(post7);
        listAlex.add(post6);


        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post1.getUser().getUsername())).thenReturn(listTedi);
        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post2.getUser().getUsername())).thenReturn(listTedi);
        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post3.getUser().getUsername())).thenReturn(listUser1);
        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post6.getUser().getUsername())).thenReturn(listAlex);
        Mockito.when(postRepository.findAllByUserUsername
                (Sort.by(Sort.Direction.DESC, "postId"),
                        post7.getUser().getUsername())).thenReturn(listAlex);

        Mockito.when(userService.getUserByUserName(principal.getName())).thenReturn(userTedi);
        Mockito.when(postRepository.findAll()).thenReturn(listAll);

        //act
        List<Post> listResult = mockPostService.findPostsByAuthority
                (Sort.by(Sort.Direction.DESC, "postId"), principal);

        //assert
        assertEquals(5, listResult.size());
        assertEquals(post3, listResult.get(0));
        assertEquals(post7, listResult.get(1));
        assertEquals(post2, listResult.get(2));
        assertEquals(post6, listResult.get(3));
        assertEquals(post1, listResult.get(4));
    }

    @Test
    public void filterPostsByPublicityShould_ReturnFilteredPosts() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        post1.setDate("20/04/2020 14:34:13");
        Post post2 = FactoryPostComment.createPost();
        post2.setDate("20/04/2020 15:34:13");
        post2.setPublic(false);
        Post post3 = FactoryPostComment.createPost();
        post3.setDate("20/04/2020 16:34:13");


        List<Post> list = new ArrayList<>();
        list.add(post1);
        list.add(post2);
        list.add(post3);

        //act
        List<Post> listResult = mockPostService.filterPostsByPublicity(list, true);

        //assert
        assertEquals(2, listResult.size());
        assertEquals(post1, listResult.get(0));
        assertEquals(post3, listResult.get(1));
    }

    @Test
    @Ignore
    public void findPostsByAuthorityShould_ReturnPublicPosts_WhenPrincipalNull() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        Post post2 = FactoryPostComment.createPost();
        post2.setPublic(false);
        Post post3 = FactoryPostComment.createPost();

        List<Post> list = new ArrayList<>();
        list.add(post3);
        list.add(post2);
        list.add(post1);

        Mockito.when(postRepository.findAll(Sort.by
                (Sort.Direction.DESC, "postId"))).thenReturn(list);
        //act
        List<Post> listResult = mockPostService.findPostsByAuthority
                (Sort.by(Sort.Direction.DESC, "postId"), null);

        //assert
        assertEquals(2, listResult.size());
        assertEquals(post3, listResult.get(0));
        assertEquals(post1, listResult.get(1));
    }

    @Test
    public void filterPostsByCategoryShould_ReturnFilteredPosts() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        Post post2 = FactoryPostComment.createPost();
        Post post3 = FactoryPostComment.createPost();

        User user1 = Factory.createUser();
        user1.setUsername("User1");
        user1.getExpertiseProfile().setCategory(new Category("Lawyer"));
        User user2 = Factory.createUser();
        user2.setUsername("User2");
        user2.getExpertiseProfile().setCategory(new Category("Lawyer"));
        User user3 = Factory.createUser();
        user3.setUsername("User3");
        user3.getExpertiseProfile().setCategory(new Category("Lifeguard"));

        post1.setUser(user1);
        post2.setUser(user2);
        post3.setUser(user3);

        List<Post> list = new ArrayList<>();
        list.add(post1);
        list.add(post2);
        list.add(post3);

        //act
        List<Post> listResult = mockPostService.filterPostsByCategory(list, "Lawyer");

        //assert
        assertEquals(2, listResult.size());
        assertEquals(post1, listResult.get(0));
        assertEquals(post2, listResult.get(1));
    }

    @Test
    public void filterPostsByUsernameShould_ReturnFilteredPosts() {
        //arrange
        Post post1 = FactoryPostComment.createPost();
        Post post2 = FactoryPostComment.createPost();
        post2.setPublic(false);
        Post post3 = FactoryPostComment.createPost();

        User user1 = Factory.createUser();
        user1.setUsername("User1");
        User user2 = Factory.createUser();
        user2.setUsername("User2");
        User user3 = Factory.createUser();
        user3.setUsername("User2");

        post1.setUser(user1);
        post2.setUser(user2);
        post3.setUser(user3);

        List<Post> list = new ArrayList<>();
        list.add(post1);
        list.add(post2);
        list.add(post3);

        //act
        List<Post> listResult = mockPostService.filterPostsByUsername(list, "User2");

        //assert
        assertEquals(2, listResult.size());
        assertEquals(post2, listResult.get(0));
        assertEquals(post3, listResult.get(1));
    }

    @Test
    public void getOneShould_CallRepositoryIfPostExists() {
        //arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        Principal principal = () -> "tedi";

        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.when(postRepository.getOne(1)).thenReturn(post);

        //act
        mockPostService.getOne(1, principal);
        //assert
        Mockito.verify(postRepository, Mockito.times(1)).getOne(1);
    }

    @Test
    public void getOneShould_ThrowIfPostDoesNotExists() {
        //arrange
        Principal principal = () -> "tedi";
        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.getOne(1, principal));
    }

    @Test
    public void existsByIdShould_ReturnIfPostExists() {
        //arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);

        Mockito.when(postRepository.existsById(1)).thenReturn(true);

        //act
        mockPostService.existsById(1);
        //assert
        assertTrue(mockPostService.existsById(1));
    }

    @Test
    public void likePostShould_AddLike() {
        //arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        Principal principal = () -> "tedi";

        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.when(postRepository.getOne(1)).thenReturn(post);

        //act
        mockPostService.likePost(1, principal);
        //assert
        assertEquals(1, post.getLikes().size());
    }

    @Test
    public void likePostShould_Throw_WhenPostDoesNotExist() {
        //Arrange
        Principal principal = () -> "tedi";

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.likePost(1, principal));
    }

    @Test
    public void likePostShould_Throw_WhenPostAlreadyLiked() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        Principal principal = () -> "tedi";

        User user = userService.getUserByUserName(principal.getName());
        post.getLikes().add(user);

        Mockito.when(postRepository.existsById(post.getPostId())).thenReturn(true);
        Mockito.when(postRepository.getOne(post.getPostId())).thenReturn(post);

        //Act, Assert
        Assert.assertThrows(DuplicateEntityException.class,
                () -> mockPostService.likePost(post.getPostId(), principal));
    }

    @Test
    public void likePostShould_Throw_IfPrincipalIsNull() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPublic(false);

        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockPostService.likePost(1, null));
    }

    @Test
    public void Save_Throw_IfPrincipalIsNull() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPublic(false);

        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockPostService.save(post, null));
    }

    @Test
    public void Save_Throw_IfContentLongerThan1000() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        post.setContent(new String(new char[1001]));

        Principal principal = () -> "tedi";

        //Assert

        //Act
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockPostService.save(post, principal));
    }

    @Test
    public void Save_Should_CallRepository() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        post.setContent("real Content");

        Principal principal = () -> "tedi";

        //Assert
        mockPostService.save(post, principal);

        //Act
        Mockito.verify(postRepository, Mockito.times(1)).save(post);
    }

    @Test
    public void dislikePostShould_RemoveLike() {
        //arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);

        Principal principal = () -> "tedi";
        User user = userService.getUserByUserName(principal.getName());
        post.getLikes().add(user);

        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.when(postRepository.getOne(1)).thenReturn(post);

        //act
        mockPostService.dislikePost(1, principal);
        //assert
        assertEquals(0, post.getLikes().size());
    }

    @Test
    public void dislikePostShould_Throw_WhenPostDoesNotExist() {
        //Arrange
        Principal principal = () -> "tedi";

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.dislikePost(1, principal));
    }

    @Test
    public void dislikePostShould_Throw_WhenPostIsNotLiked() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        Principal principal = () -> "tedi";

        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.when(postRepository.getOne(1)).thenReturn(post);

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.dislikePost(1, principal));
    }

    @Test
    public void dislikePostShould_Throw_IfPrincipalIsNull() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPublic(false);

        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockPostService.dislikePost(1, null));
    }

    @Test
    public void isLikedShould_ReturnCorrect() {
        //arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        User user = Factory.createUser();
        post.getLikes().add(user);

        Principal principal = () -> "tedi";
        Mockito.when(postRepository.getOne(1)).thenReturn(post);

        //Act, Assert
        assertTrue(mockPostService.isLiked(post.getPostId(), principal));
    }

    @Test
    public void editPostShould_Throw_WhenPostDoesNotExists() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);

        PostDTO postDTO = new PostDTO();

        //Assert
        Principal principal = () -> "tedi";

        //Act
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.editPost(post.getPostId(), postDTO, principal));
    }

    @Test
    public void editPostShould_Throw_WhenContentLongerThan1000() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);

        PostDTO postDTO = new PostDTO();
        postDTO.setContent(new String(new char[1001]));
        Principal principal = () -> "tedi";

        //Assert
        Mockito.when(postRepository.existsById(1)).thenReturn(true);

        //Act
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockPostService.editPost(post.getPostId(), postDTO, principal));
    }

    @Test
    public void editPostShould_Edit() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        post.setPicture("picture");
        PostDTO postDTO = new PostDTO();
        postDTO.setPicture(new String(new char[501]));
        postDTO.setContent("content");
        Principal principal = () -> "tedi";

        List<User> list = new ArrayList<>();

        Mockito.when(postRepository.getOne(1)).thenReturn(post);
        Mockito.when(postRepository.existsById(1)).thenReturn(true);

        //Act
        mockPostService.editPost(post.getPostId(), postDTO, principal);

        //Assert
        assertEquals(501, post.getPicture().length());
    }

    @Test
    public void editPostShould_Throw_WhenUserIsNotAllowed() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        post.setPicture("picture");
        PostDTO postDTO = new PostDTO();
        postDTO.setContent("content");
        postDTO.setPicture(new String(new char[501]));
        Principal principal = () -> "xxx";
        List<User> list = new ArrayList<>();

        Mockito.when(postRepository.getOne(1)).thenReturn(post);
        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.doThrow(new InvalidOperationException())
                .when(userService).ifNotProfileOrAdminOwnerThrow(principal.getName(), post.getUser());

        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockPostService.editPost(post.getPostId(), postDTO, principal));
    }

    @Test
    public void deletePostShould_Delete() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        Principal principal = () -> "tedi";

        List<User> list = new ArrayList<>();

        Mockito.when(postRepository.getOne(1)).thenReturn(post);
        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.when(commentService.deleteCommentByPostPostId(post.getPostId())).thenReturn(1);

        //Act
        mockPostService.deletePost(1, principal);

        //Assert
        Mockito.verify(postRepository, Mockito.times(1)).delete(post);
    }

    @Test
    public void deletePostShould_Throw_WhenPostDoesNotExists() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        Principal principal = () -> "tedi";

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.deletePost(post.getPostId(), principal));
    }

    @Test
    public void deletePostShould_Throw_WhenUserIsNotAllowed() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        post.setPicture("picture");
        PostDTO postDTO = new PostDTO();
        postDTO.setPicture(new String(new char[501]));
        Principal principal = () -> "xxx";

        Mockito.when(postRepository.getOne(1)).thenReturn(post);
        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.doThrow(new InvalidOperationException())
                .when(userService).ifNotProfileOrAdminOwnerThrow(principal.getName(), post.getUser());
        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockPostService.deletePost(post.getPostId(), principal));
    }

    @Test
    public void showCommentsShould_Throw_WhenPostDoesNotExists() {
        //Arrange

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockPostService.showComments(1));
    }

    @Test
    public void showCommentsShould_ReturnComments() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(1);
        User user = Factory.createUser();

        Comment comment1 = new Comment();
        comment1.setUser(user);
        comment1.setPost(post);
        post.getComments().add(comment1);

        Mockito.when(postRepository.existsById(1)).thenReturn(true);
        Mockito.when(postRepository.getOne(1)).thenReturn(post);

        //Act, Assert
        assertEquals(comment1, mockPostService.showComments(post.getPostId()).get(0));
    }

    @Test
    public void ifNotAuthorizedToVewPostThrow_IfPrincipalIsNullAndPostNotPublic() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPublic(false);


        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockPostService.ifNotAuthorizedToVewPostThrow(null, post));
    }

    @Test
    public void ifNotAuthorizedToVewPostThrow_IfNotAuthorized() {
        //Arrange
        Post post = FactoryPostComment.createPost();
        post.setPublic(false);
        User user = Factory.createUser();
        User user2 = Factory.createUser();
        user2.setUsername("newPerson");
        post.setUser(user);
        user.getFriendList().add(user2);
        Principal principal = () -> "xxx";

        //Assert
        Mockito.when(userService.getUserByUserName(post.getUser().getUsername())).thenReturn(user);
        //Act

        Assert.assertThrows(InvalidOperationException.class,
                () -> mockPostService.ifNotAuthorizedToVewPostThrow(principal, post));
    }
}
