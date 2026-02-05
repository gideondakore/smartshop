package com.amalitech.smartshop.interfaces;

import com.amalitech.smartshop.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Repository interface for Category entity operations.
 */
public interface CategoryRepository {

    /**
     * Check if a category exists with the given name (case-insensitive).
     *
     * @param name the category name to check
     * @return true if a category with the name exists
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find a category by name (case-insensitive).
     *
     * @param name the category name to search for
     * @return an Optional containing the category if found
     */
    Optional<Category> findByNameIgnoreCase(String name);

    /**
     * Find a category by its ID.
     *
     * @param id the category ID
     * @return an Optional containing the category if found
     */
    Optional<Category> findById(Long id);

    /**
     * Save or update a category.
     *
     * @param category the category to save
     * @return the saved category
     */
    Category save(Category category);

    /**
     * Delete a category.
     *
     * @param category the category to delete
     */
    void delete(Category category);

    /**
     * Find all categories with pagination.
     *
     * @param pageable pagination information
     * @return a page of categories
     */
    Page<Category> findAll(Pageable pageable);

    /**
     * Count all categories.
     *
     * @return the total number of categories
     */
    long count();
}
