package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.PostBookmark;
import com.skuniv.fuwarilog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostBookmarkRepository extends JpaRepository<PostBookmark,Long> {
    PostBookmark findByUserIdAndPostId(Long userId, Long postId);

    List<PostBookmark> findAllByUser(User user);
}
