package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.PostBookmark;
import com.skuniv.fuwarilog.domain.User;
import com.skuniv.fuwarilog.dto.PostBookmark.PostBookmarkResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostBookmarkRepository extends JpaRepository<PostBookmark,Long> {
    PostBookmark findByUserIdAndPostId(long userId, long postId);

    List<PostBookmark> findAllByUser(User user);
}
