package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.Diary;
import com.skuniv.fuwarilog.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findAllByTripId(Long tripId);

    Optional<Diary> findByTrip(Trip trip);
}
