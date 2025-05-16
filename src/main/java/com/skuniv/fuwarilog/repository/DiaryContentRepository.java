package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.DiaryContent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DiaryContentRepository extends MongoRepository<DiaryContent, String> {
    List<DiaryContent> findByDiaryListId(Long diaryListId);
}
