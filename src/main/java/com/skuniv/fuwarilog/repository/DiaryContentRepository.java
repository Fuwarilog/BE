package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.DiaryContent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryContentRepository extends MongoRepository<DiaryContent, String> {
    Optional<DiaryContent> findByUserIdAndDiaryListId(Long userId, Long diaryListId);

    List<DiaryContent> findByUserIdAndTripDate(Long userId, LocalDate now);

    void deleteByDiaryListIdAndTripDate(long id, LocalDate date);
}