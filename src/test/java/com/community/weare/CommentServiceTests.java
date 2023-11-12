package com.community.weare;

import com.community.weare.Exceptions.DuplicateEntityException;
import com.community.weare.Exceptions.EntityNotFoundException;
import com.community.weare.Exceptions.InvalidOperationException;
import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Repositories.CommentRepository;
import com.community.weare.Repositories.PostRepository;
import com.community.weare.Services.contents.CommentServiceImpl;
import com.community.weare.Services.contents.PostService;
import com.community.weare.Services.users.UserServiceImpl;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class CommentServiceTests {
    @InjectMocks
    CommentServiceImpl mockCommentService;

    @Mock
    UserServiceImpl userService;
    @Mock
    PostService postService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    PostRepository postRepository;

    @Test
    public void findAllShould_CallRepository() {
        //arrange
        Comment comment1 = FactoryPostComment.createComment();
        Comment comment2 = FactoryPostComment.createComment();
        List<Comment> list = new ArrayList<>();
        list.add(comment1);
        list.add(comment2);

        Mockito.when(commentRepository.findAll()).thenReturn(list);
        //act
        mockCommentService.findAll();
        //assert
        Mockito.verify(commentRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void findAllShould_SortPostsByDate() {
        //arrange
        Comment comment1 = FactoryPostComment.createComment();
        comment1.setDate("1");
        Comment comment2 = FactoryPostComment.createComment();
        comment1.setDate("2");
        Comment comment3 = FactoryPostComment.createComment();
        comment1.setDate("3");

        List<Comment> list = new ArrayList<>();
        list.add(comment3);
        list.add(comment1);
        list.add(comment2);

        List<Comment> listSorted = new ArrayList<>();
        listSorted.add(comment1);
        listSorted.add(comment2);
        listSorted.add(comment3);

        Mockito.when(commentRepository.findAll(Sort.by
                (Sort.Direction.ASC, "commentId"))).thenReturn(listSorted);
        //act
        List<Comment> listResult = mockCommentService.findAll
                (Sort.by(Sort.Direction.ASC, "commentId"));
        //assert
        assertEquals(list.get(0), listResult.get(2));
        assertEquals(list.get(1), listResult.get(0));
        assertEquals(list.get(2), listResult.get(1));
    }

    @Test
    public void findAllCommentsOfPostShould_ThrowIfPostDoesNotExists() {
        //arrange
        Post post = FactoryPostComment.createPost();

        Mockito.when(postRepository.existsById(post.getPostId())).thenReturn(false);
        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockCommentService.findAllCommentsOfPost
                        (post, Sort.by(Sort.Direction.ASC, "commentId")));
    }

    //findAllCommentsOfPost
    @Test
    public void findAllCommentsOfPost_CallRepository() {
        //arrange
        Post post = FactoryPostComment.createPost();
        Comment comment1 = FactoryPostComment.createComment();
        Comment comment2 = FactoryPostComment.createComment();
        List<Comment> list = new ArrayList<>();
        list.add(comment1);
        list.add(comment2);

        Mockito.when(postRepository.existsById(post.getPostId())).thenReturn(true);
        Mockito.when(commentRepository.findByPostOrderByCommentId
                (post, Sort.by(Sort.Direction.ASC, "commentId"))).thenReturn(list);

        //act
        mockCommentService.findAllCommentsOfPost(post, Sort.by(Sort.Direction.ASC, "commentId"));
        //assert
        Mockito.verify(commentRepository, Mockito.times(1))
                .findByPostOrderByCommentId(post, Sort.by(Sort.Direction.ASC, "commentId"));
    }

    @Test
    public void existsByIdShould_ReturnIfCommentExists() {
        //arrange
        Comment comment = FactoryPostComment.createComment();

        Mockito.when(commentRepository.existsById(comment.getCommentId())).thenReturn(true);

        //act
        mockCommentService.existsById(comment.getCommentId());
        //assert
        assertTrue(mockCommentService.existsById(comment.getCommentId()));
    }

    @Test
    public void getOneShould_CallRepositoryIfPostExists() {
        //arrange
        Comment comment = FactoryPostComment.createComment();

        Mockito.when(commentRepository.existsById(comment.getCommentId())).thenReturn(true);
        Mockito.when(commentRepository.getOne(comment.getCommentId())).thenReturn(comment);

        //act
        mockCommentService.getOne(comment.getCommentId());
        //assert
        Mockito.verify(commentRepository, Mockito.times(1)).getOne(comment.getCommentId());
    }

    @Test
    public void getOneShould_ThrowIfPostDoesNotExists() {
        //arrange

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockCommentService.getOne(1));
    }

    @Test
    public void saveShould_CallTwoRepositories() {
        //arrange
        Comment comment = FactoryPostComment.createComment();
        Post post = FactoryPostComment.createPost();
        comment.setPost(post);
        post.getComments().add(comment);
        Principal principal = () -> "tedi";

        //act
        mockCommentService.save(comment, principal);
        //assert
        Mockito.verify(commentRepository, Mockito.times(1)).save(comment);
        Mockito.verify(postRepository, Mockito.times(1)).save(post);
    }

    @Test
    public void saveShould_ThrowIfPrincipalIsNull() {
        //Arrange
        Comment comment = FactoryPostComment.createComment();

        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockCommentService.save(comment, null));
    }

    @Test
    public void Save_Throw_IfContentLongerThan1000() {
        //Arrange
        Comment comment = FactoryPostComment.createComment();
        comment.setContent(new String(new char[1001]));

        Principal principal = () -> "tedi";

        //Assert

        //Act
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockCommentService.save(comment, principal));
    }

    @Test
    public void likeCommentShould_ThrowIfPrincipalIsNull() {
        //Arrange
        Comment comment = FactoryPostComment.createComment();

        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockCommentService.likeComment(comment.getCommentId(), null));
    }

    @Test
    public void dislikeCommentShould_ThrowIfPrincipalIsNull() {
        //Arrange
        Comment comment = FactoryPostComment.createComment();

        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockCommentService.dislikeComment(comment.getCommentId(), null));
    }

    @Test
    public void editCommentShould_ThrowIfPrincipalIsNull() {
        //Arrange
        Comment comment = FactoryPostComment.createComment();
        comment.setContent(new String(new char[100]));

        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockCommentService.editComment(comment.getCommentId(), comment.getContent(), null));
    }

    @Test
    public void editCommentShould_ThrowIfContentLongerThan1000() {
        //Arrange
        Comment comment = FactoryPostComment.createComment();
        comment.setContent(new String(new char[1001]));
        Principal principal = () -> "tedi";
        Mockito.when(commentRepository.existsById(comment.getCommentId())).thenReturn(true);

        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockCommentService.editComment(comment.getCommentId(), comment.getContent(), principal));
    }

    @Test
    public void likeCommentShould_AddLike() {
        //arrange

        Comment comment = FactoryPostComment.createComment();
        Principal principal = () -> "tedi";

        Mockito.when(commentRepository.existsById(comment.getCommentId())).thenReturn(true);
        Mockito.when(commentRepository.getOne(comment.getCommentId())).thenReturn(comment);

        //act
        mockCommentService.likeComment(comment.getCommentId(), principal);
        //assert
        assertEquals(1, comment.getLikes().size());
    }

    @Test
    public void likeCommentShould_Throw_WhenCommentDoesNotExist() {
        //Arrange
        Principal principal = () -> "tedi";

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockCommentService.likeComment(1, principal));
    }

    @Test
    public void likeCommentShould_Throw_WhenCommentAlreadyLiked() {
        //Arrange
        Comment comment = FactoryPostComment.createComment();
        Principal principal = () -> "tedi";
        User user = userService.getUserByUserName(principal.getName());
        comment.getLikes().add(user);

        Mockito.when(commentRepository.existsById(1)).thenReturn(true);
        Mockito.when(commentRepository.getOne(1)).thenReturn(comment);

        //Act, Assert
        Assert.assertThrows(DuplicateEntityException.class,
                () -> mockCommentService.likeComment(1, principal));
    }

    @Test
    public void dislikeCommentShould_RemoveLike() {
        //arrange
        Comment comment = FactoryPostComment.createComment();
        Principal principal = () -> "tedi";
        User user = userService.getUserByUserName(principal.getName());
        comment.getLikes().add(user);

        Mockito.when(commentRepository.existsById(comment.getCommentId())).thenReturn(true);
        Mockito.when(commentRepository.getOne(comment.getCommentId())).thenReturn(comment);

        //act
        mockCommentService.dislikeComment(comment.getCommentId(), principal);
        //assert
        assertEquals(0, comment.getLikes().size());
    }

    @Test
    public void dislikeCommentShould_Throw_WhenCommentDoesNotExist() {
        //Arrange
        Principal principal = () -> "tedi";

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockCommentService.dislikeComment(1, principal));
    }

    @Test
    public void dislikeCommentShould_Throw_WhenCommentIsNotLiked() {
        //Arrange
        Comment comment = FactoryPostComment.createComment();
        Principal principal = () -> "tedi";

        Mockito.when(commentRepository.existsById(comment.getCommentId())).thenReturn(true);
        Mockito.when(commentRepository.getOne(comment.getCommentId())).thenReturn(comment);

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockCommentService.dislikeComment(comment.getCommentId(), principal));
    }

    @Test
    public void editCommentShould_Throw_WhenCommentDoesNotExists() {
        //Arrange
        Comment comment = FactoryPostComment.createComment();
        Principal principal = () -> "tedi";

        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockCommentService.editComment(1, "newContent", principal));
    }

    @Test
    public void editCommentShould_Throw_WhenUserIsNotAllowed() {
        //Arrange
        Comment comment = FactoryPostComment.createComment();
        comment.setContent("oldContent");

        User user = Factory.createUser();
        comment.setUser(user);
        Principal principal = () -> "xxx";

        Mockito.when(commentRepository.getOne(comment.getCommentId())).thenReturn(comment);
        Mockito.when(commentRepository.existsById(comment.getCommentId())).thenReturn(true);
        Mockito.doThrow(new InvalidOperationException())
                .when(userService).ifNotProfileOrAdminOwnerThrow(principal.getName(), comment.getUser());
        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockCommentService.editComment(comment.getCommentId(), "newContent", principal));
    }

    @Test
    public void editCommentShould_Edit() {
        //Arrange
        Comment comment = FactoryPostComment.createComment();
        comment.setContent("contentFirst");
        User userTedi = Factory.createUser();
        comment.setUser(userTedi);
        Principal principal = () -> "tedi";

        Mockito.when(commentRepository.getOne(comment.getCommentId())).thenReturn(comment);
        Mockito.when(commentRepository.existsById(comment.getCommentId())).thenReturn(true);

        //Act
        mockCommentService.editComment(comment.getCommentId(), "contentSecond", principal);

        //Assert
        assertEquals("contentSecond", comment.getContent());
    }

    @Test
    public void deleteCommentByPostPostIdShould_CallRepository() {
        //arrange
        Post post = FactoryPostComment.createPost();
        post.setPostId(6);
        Comment comment1 = FactoryPostComment.createComment();
        comment1.setPost(post);
        post.getComments().add(comment1);

        Mockito.when(postRepository.existsById(comment1.getPost().getPostId())).thenReturn(true);

        //act
        mockCommentService.deleteCommentByPostPostId(comment1.getPost().getPostId());
        //assert
        Mockito.verify(commentRepository, Mockito.times(1))
                .deleteCommentByPostPostId(comment1.getCommentId());
    }

    @Test
    @Ignore
    public void deleteCommentShould_Delete() {
        //Arrange
        Comment comment1 = FactoryPostComment.createComment();
        User user = Factory.createUser();
        comment1.setUser(user);
        Principal principal = () -> "tedi";

        Mockito.when(commentRepository.getOne(comment1.getCommentId())).thenReturn(comment1);
        Mockito.when(commentRepository.existsById(comment1.getCommentId())).thenReturn(true);

        //Act
        mockCommentService.deleteComment(comment1.getCommentId(), principal);

        //Assert
        Mockito.verify(commentRepository, Mockito.times(1)).delete(comment1);
    }

    @Test
    public void deleteCommentShould_ThrowIfCommentDoesNotExists() {
        //arrange
        Comment comment1 = FactoryPostComment.createComment();
        Principal principal = () -> "tedi";

        Mockito.when(commentRepository.existsById(comment1.getCommentId())).thenReturn(false);
        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockCommentService.deleteComment(comment1.getCommentId(), principal));
    }

    @Test
    public void deleteCommentShould_ThrowIfPrincipalIsNull() {
        //Arrange
        Comment comment = FactoryPostComment.createComment();
        comment.setContent(new String(new char[100]));

        //Act, Assert
        Assert.assertThrows(InvalidOperationException.class,
                () -> mockCommentService.deleteComment(comment.getCommentId(), null));
    }

    @Test
    public void deleteCommentByPostPostIdShould_ThrowIfCommentDoesNotExists() {
        //arrange
        Comment comment1 = FactoryPostComment.createComment();
        Post post = FactoryPostComment.createPost();
        comment1.setPost(post);

        Mockito.when(postRepository.existsById(comment1.getPost().getPostId())).thenReturn(false);
        //Act, Assert
        Assert.assertThrows(EntityNotFoundException.class,
                () -> mockCommentService.deleteCommentByPostPostId(post.getPostId()));
    }

}
