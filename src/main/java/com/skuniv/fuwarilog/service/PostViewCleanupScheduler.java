package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.repository.PostViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewCleanupScheduler {
    private final PostViewRepository postViewRepository;

    /**
     * @implSpec 데이터 스케쥴러 - 조회수 정보 테이블
     * 매일 00:00 post_view 테이블 업데이트
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupOldView() {
        LocalDate threshold = LocalDate.now().minusDays(7);
        postViewRepository.deleteOldViews(threshold);
        log.info("오래된 데이터 삭제(7일 이상) " + threshold);
    }
}
