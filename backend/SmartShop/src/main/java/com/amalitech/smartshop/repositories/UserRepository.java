package com.amalitech.smartshop.repositories;

import com.amalitech.smartshop.entities.User;
import com.amalitech.smartshop.enums.UserRole;
import com.amalitech.smartshop.exceptions.BadRequestFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-based implementation of the UserRepository interface.
 */
@Repository
public class UserRepository implements com.amalitech.smartshop.interfaces.UserRepository {
    private final Connection connection;

    public UserRepository(Connection connection) {
        this.connection = connection;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("id"))
                .firstName(rs.getString("first_name"))
                .lastName(rs.getString("last_name"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .role(UserRole.valueOf(rs.getString("role")))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by email", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user by id", e);
        }
        return Optional.empty();
    }

    @Override
    public User save(User user) {
        try {
            if (user.getId() == null) {
                return insert(user);
            } else {
                return updateUser(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving user", e);
        }
    }

    @Override
    public User update(User user) {
        try{
            if(user.getId() == null){
                throw new BadRequestFormat("User not found. Please login again to try");
            }else{
                return updateUser(user);
            }
        }catch (SQLException e){
            throw new RuntimeException("Error occur whilst updating the user");
        }

    }

    private User insert(User user) throws SQLException {
        String sql = "INSERT INTO users (first_name, last_name, email, password, role, created_at, updated_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }
        }
        return user;
    }

    private User updateUser(User user) throws SQLException {
    String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, role = ?, updated_at = NOW() WHERE id = ?";
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, user.getFirstName());
        ps.setString(2, user.getLastName());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getRole().name());
        ps.setLong(5, user.getId());
        ps.executeUpdate();
    }
    return user;
}


    @Override
    public void delete(User user) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        List<User> users = new ArrayList<>();
        String sql;
        
        if (pageable.isUnpaged()) {
            sql = "SELECT * FROM users";
            try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error finding all users", e);
            }
        } else {
            sql = "SELECT * FROM users";
            
            if (pageable.getSort().isSorted()) {
                sql += " ORDER BY ";
                sql += pageable.getSort().stream()
                    .map(order -> {
                        String column = mapPropertyToColumn(order.getProperty());
                        return column + " " + order.getDirection().name();
                    })
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("id ASC");
            }
            
            sql += " LIMIT ? OFFSET ?";
            
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, pageable.getPageSize());
                ps.setInt(2, (int) pageable.getOffset());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        users.add(mapRow(rs));
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error finding all users", e);
            }
        }
        
        long total = countUsers();
        return new PageImpl<>(users, pageable, total);
    }
    
    private String mapPropertyToColumn(String property) {
        return switch (property) {
            case "firstName" -> "first_name";
            case "lastName" -> "last_name";
            case "createdAt" -> "created_at";
            case "updatedAt" -> "updated_at";
            default -> property;
        };
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all users", e);
        }
        return users;
    }

    private long countUsers() {
        String countSql = "SELECT COUNT(*) FROM users";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(countSql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error counting users", e);
        }
        return 0;
    }

    @Override
    public long count() {
        return countUsers();
    }
}
