package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.Diary;
import com.skuniv.fuwarilog.domain.DiaryList;
import com.skuniv.fuwarilog.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DiaryListRepository extends JpaRepository<DiaryList, Long> {
    List<DiaryList> findAllByDiaryIdOrderByDateAsc(Long diaryId);

    void deleteByDiaryAndDate(Diary diary, LocalDate d);

    List<DiaryList> findByDiary(Diary diary);
}
