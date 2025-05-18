package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.VisitedRouteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface VisitedRouteRepository extends MongoRepository<VisitedRouteDocument, String> {
    List<VisitedRouteDocument> findByUserIdAndTripDate(Long userId, LocalDate tripDate);
}