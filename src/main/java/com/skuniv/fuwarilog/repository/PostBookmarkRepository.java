package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.PostBookmark;
import com.skuniv.fuwarilog.dto.PostBookmark.PostBookmarkResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostBookmarkRepository extends JpaRepository<PostBookmark,Long> {
    PostBookmark findByUserIdAndPostId(long userId, long postId);
}
