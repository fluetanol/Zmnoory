package com.gradation.zmnnoory.domain.comment.repository;

import com.gradation.zmnnoory.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByVideoIdOrderByCreatedAtAsc(Long videoId);
    
    long countByVideoId(Long videoId);
}
