package com.amalitech.smartshop.config;

import com.amalitech.smartshop.interfaces.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SessionCleanupTask {

    private final SessionRepository sessionRepository;

    @Scheduled(cron = "0 0 * * * *") // Every hour
    public void cleanupExpiredSessions() {
        log.info("Running session cleanup task");
        sessionRepository.deleteExpiredSessions();
        log.info("Expired sessions cleaned up");
    }
}
