package com.skuniv.fuwarilog.repository;

import com.skuniv.fuwarilog.domain.PostView;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PostViewRepository extends JpaRepository<PostView,Long> {

    boolean existsByUserIdAndPostIdAndViewDate(Long userId, Long postId, LocalDate today);
    boolean existsByIpAddressAndPostIdAndViewDate(String ipAddress, Long postId, LocalDate today);

    @Modifying
    @Transactional
    @Query("DELETE FROM PostView pv WHERE pv.viewDate < :threshold")
    void deleteOldViews(@Param("threshold") LocalDate threshold);
}
