package com.amalitech.smartshop.repositories;

import com.amalitech.smartshop.entities.Session;
import com.amalitech.smartshop.interfaces.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepository {

    private final Connection connection;

    @Override
    public Session save(Session session) {
        String sql = "INSERT INTO sessions (token, user_id, expires_at) VALUES (?, ?, ?) RETURNING id, created_at";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, session.getToken());
            stmt.setLong(2, session.getUserId());
            stmt.setTimestamp(3, Timestamp.valueOf(session.getExpiresAt()));
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                session.setId(rs.getLong("id"));
                session.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            return session;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving session", e);
        }
    }

    @Override
    public Optional<Session> findByToken(String token) {
        String sql = "SELECT * FROM sessions WHERE token = ? AND expires_at > NOW()";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding session by token", e);
        }
    }

    @Override
    public List<Session> findByUserId(Long userId) {
        String sql = "SELECT * FROM sessions WHERE user_id = ? AND expires_at > NOW()";
        List<Session> sessions = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sessions.add(mapRow(rs));
            }
            return sessions;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding sessions by user", e);
        }
    }

    @Override
    public void deleteByToken(String token) {
        String sql = "DELETE FROM sessions WHERE token = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting session", e);
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        String sql = "DELETE FROM sessions WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user sessions", e);
        }
    }

    @Override
    public void deleteExpiredSessions() {
        String sql = "DELETE FROM sessions WHERE expires_at < NOW()";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting expired sessions", e);
        }
    }

    private Session mapRow(ResultSet rs) throws SQLException {
        return Session.builder()
                .id(rs.getLong("id"))
                .token(rs.getString("token"))
                .userId(rs.getLong("user_id"))
                .expiresAt(rs.getTimestamp("expires_at").toLocalDateTime())
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
