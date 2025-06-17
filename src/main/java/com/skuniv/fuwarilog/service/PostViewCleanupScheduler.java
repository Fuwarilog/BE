package com.skuniv.fuwarilog.service;

import com.skuniv.fuwarilog.repository.PostViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class PostViewCleanupScheduler {
    private final PostViewRepository postViewRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupOldView() {
        LocalDate threshold = LocalDate.now().minusDays(7);
        postViewRepository.deleteOldViews(threshold);
    }
}
