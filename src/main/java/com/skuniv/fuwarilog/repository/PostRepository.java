package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.DiaryList;
import com.skuniv.fuwarilog.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    Post findByDiaryList(DiaryList diaryList);
}
