package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.Trip;
import com.skuniv.fuwarilog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findAllByUser(User user);

    List<Trip> findAllById(Long tripId);

    List<Trip> findAllByUserId(Long userId);
}
