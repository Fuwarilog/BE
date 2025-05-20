package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.Diary;
import com.skuniv.fuwarilog.domain.DiaryList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryListRepository extends JpaRepository<DiaryList, Long> {
    List<DiaryList> findAllByDiary(Diary diary);
}
