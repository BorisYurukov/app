package com.community.weare.Repositories;

import com.community.weare.Models.Comment;
import com.community.weare.Models.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {
    List<Comment> findByPostOrderByCommentId(Post post, Sort sort);

    @Transactional
    Integer deleteCommentByPostPostId(int postId);
}
