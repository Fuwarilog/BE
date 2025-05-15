package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.DiaryList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryListRepository extends JpaRepository<DiaryList, Long> {
}
