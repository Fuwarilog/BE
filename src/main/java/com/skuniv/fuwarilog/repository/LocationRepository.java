package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Map;

public interface LocationRepository extends JpaRepository<Location,String> {
}
