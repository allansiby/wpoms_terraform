package com.wpoms.admin.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wpoms.admin.models.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Find products by manufacturer ID
    List<Product> findByManufacturerId(int manufacturerId);

    // Check if product exists for a manufacturer
    boolean existsByProductNameAndManufacturerId(String productName, int manufacturerId);

    // Find product by ID and manufacturer ID
    Optional<Product> findByProductIdAndManufacturerId(int productId, int manufacturerId);
}