package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findUserIdAndDate(Long userId, LocalDate date);
}
