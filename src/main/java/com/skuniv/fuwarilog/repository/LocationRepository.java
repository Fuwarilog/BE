package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location,String> {
    Location findByIdAndUserId(Long locationId, Long userId);

    Location findByUserIdAndPlaceId(Long attr0, String placeId);
}
