package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface LocationRepository extends JpaRepository<Location,String> {
    List<Location> findByUserIdAndBookmarkedAt(Long userId, LocalDateTime localDateTime, LocalDateTime localDateTime1);
}
