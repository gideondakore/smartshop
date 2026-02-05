package com.amalitech.smartshop.services;

import com.amalitech.smartshop.entities.Session;
import com.amalitech.smartshop.interfaces.SessionRepository;
import com.amalitech.smartshop.interfaces.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private static final int SESSION_DURATION_HOURS = 24;

    @Override
    public String createSession(Long userId) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(SESSION_DURATION_HOURS);
        
        Session session = Session.builder()
                .token(token)
                .userId(userId)
                .expiresAt(expiresAt)
                .build();
        
        sessionRepository.save(session);
        log.info("Created session for user: {}, expires at: {}", userId, expiresAt);
        return token;
    }

    @Override
    public Optional<Session> validateSession(String token) {
        return sessionRepository.findByToken(token);
    }

    @Override
    public void deleteSession(String token) {
        sessionRepository.deleteByToken(token);
        log.info("Deleted session: {}", token);
    }

    @Override
    public void deleteAllUserSessions(Long userId) {
        sessionRepository.deleteByUserId(userId);
        log.info("Deleted all sessions for user: {}", userId);
    }
}
