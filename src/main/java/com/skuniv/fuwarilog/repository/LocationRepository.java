package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LocationRepository extends JpaRepository<Location,String> {
    Location findByIdAndUserId(Long locationId, Long userId);

    Location findByUserIdAndPlaceId(Long attr0, String placeId);
}
