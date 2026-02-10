package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 */
public interface UserRepository {
    
    /**
     * Find a user by their email address.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a user by their ID.
     *
     * @param id the user ID
     * @return an Optional containing the user if found
     */
    Optional<User> findById(Long id);

    /**
     * Save or update a user.
     *
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);


    User update(User user);

    /**
     * Delete a user.
     *
     * @param user the user to delete
     */
    void delete(User user);

    /**
     * Find all users with pagination.
     *
     * @param pageable pagination information
     * @return a page of users
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Find all users.
     *
     * @return list of all users
     */
    List<User> findAll();

    /**
     * Count all users.
     *
     * @return the total number of users
     */
    long count();
}
