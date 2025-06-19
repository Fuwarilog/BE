package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.PostLike;
import com.skuniv.fuwarilog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    PostLike findByUserIdAndPostId(Long userId, Long postId);

    List<PostLike> findAllByUser(User user);
}
