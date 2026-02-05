package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.entities.Session;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    Session save(Session session);
    Optional<Session> findByToken(String token);
    List<Session> findByUserId(Long userId);
    void deleteByToken(String token);
    void deleteByUserId(Long userId);
    void deleteExpiredSessions();
}
