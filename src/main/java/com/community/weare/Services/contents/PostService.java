package com.community.weare.Services.contents;

import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import com.community.weare.Models.User;
import com.community.weare.Models.dto.PostDTO;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.security.Principal;
import java.util.List;

public interface PostService {

    List<Post> findAll();

    List<Post> findAll(Sort sort);

    List<Post> findAllSortedByUserAndTime(Sort sort);

    List<Post> findAllByUser(String userName, Principal principal);

    Slice<Post> findSliceWithPosts(int pageIndex,int pageSize, String sortParam, User user,String principal );

    List<Post> findAllPostsByAlgorithm(Sort sort, Principal principal);

    List<Post> findPostsPersonalFeed(Principal principal);

    List<Post> findPostsByAuthority(Sort sort, Principal principal);

    List<Post> filterPostsByPublicity(List<Post> posts, boolean isPublic);

    List<Post> filterPostsByCategory(List<Post> posts, String categoryName);

    List<Post> filterPostsByUsername(List<Post> posts, String userName);

    List<Post> filterPostsByFriends(List<Post> posts, User user);

    List<Post> mergeTwoLists(List<Post> list1, List<Post> list2, Principal principal);

    Post getOne(int postId, Principal principal);

    boolean existsById(int postId);

    Post save(Post post, Principal principal);

    void likePost(int postId, Principal principal);

    void dislikePost(int postId, Principal principal);

    boolean isLiked(int postId, Principal principal);

    void editPost(int postId, PostDTO postDTO, Principal principal);

    void deletePost(int postId, Principal principal);

    List<Comment> showComments(int postId);

    int refreshRankOfGroupOfPosts(List<Post> posts);

    void ifNotAuthorizedToVewPostThrow(Principal principal, Post post);
}
