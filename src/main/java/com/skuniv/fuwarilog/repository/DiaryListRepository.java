package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.DiaryList;
import com.skuniv.fuwarilog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DiaryListRepository extends JpaRepository<DiaryList, Long> {
    List<DiaryList> findAllByDiaryId(Long diaryId);

}
