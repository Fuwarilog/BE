package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    PostLike findByUserIdAndPostId(Long userId, long postId);
}
