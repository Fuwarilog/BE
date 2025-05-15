package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.Trip;
import com.skuniv.fuwarilog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Optional<Trip> findAllByStartDate(LocalDate parse);

    Optional<Trip> findAllByUser(User user);

    Optional<Trip> findByIdAndStartDate(Long tripId, LocalDate parse);
}
