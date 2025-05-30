package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.Trip;
import com.skuniv.fuwarilog.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findAllByUser(User user);

    List<Trip> findAllById(Long tripId);

    List<Trip> findAllByUserId(Long userId);

    List<Trip> findAllByUserAndStartDateLessThanEqualAndEndDateGreaterThanEqual(User user, LocalDate endDate, LocalDate startDate);

    @Query(value = "select t from Trip t where t.user = :user and :today between t.startDate and t.endDate")
    List<Trip> findAllByUserAndToday(@Param("user") User user, @Param("today") LocalDate today);

    @Query("SELECT t FROM Trip t WHERE t.user = :user AND t.startDate >= :today ORDER BY t.startDate ASC")
    List<Trip> findTop3ByUserAndStartDateOrderByStartDate(@Param("user") User user, @Param("today") LocalDate today, Pageable pageable);
}
