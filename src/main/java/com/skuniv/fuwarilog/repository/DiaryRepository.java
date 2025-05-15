package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findAllByTripId(Long tripId);
}
