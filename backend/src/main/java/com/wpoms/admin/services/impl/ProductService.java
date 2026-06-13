package com.wpoms.admin.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wpoms.admin.models.entities.Product;
import com.wpoms.admin.models.payloads.ProductPayload;
import com.wpoms.admin.models.response.ProductResponse;
import com.wpoms.admin.repositories.ManufacturerMasterRepository;
import com.wpoms.admin.repositories.ProductRepository;
import com.wpoms.admin.services.IProductService;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ManufacturerMasterRepository manufacturerMasterRepository;

    // ========== 1. CREATE PRODUCT ==========
    @Override
    public ProductResponse createProduct(ProductPayload payload) {

        // Check if manufacturer exists
        if (!manufacturerMasterRepository.existsByManufacturerId(payload.getManufacturerId().intValue())) {
            throw new RuntimeException("Manufacturer not found with ID: " + payload.getManufacturerId());
        }

        // Check if product already exists for this manufacturer
        if (productRepository.existsByProductNameAndManufacturerId(
                payload.getProductName(),
                payload.getManufacturerId().intValue())) {
            throw new RuntimeException("Product " + payload.getProductName() + " already exists for this manufacturer");
        }

        // Create new product
        Product product = new Product();
        product.setProductName(payload.getProductName());
        product.setCategory(payload.getCategory());
        product.setPrice(payload.getPrice());
        product.setWarrantyType(payload.getWarrantyType());
        product.setDescription(payload.getDescription());
        product.setManufacturerId(payload.getManufacturerId().intValue());

        // SAVE the product
        Product savedProduct = productRepository.save(product);

        // Prepare response
        ProductResponse response = new ProductResponse();
        response.setProductId(savedProduct.getProductId());
        response.setProductName(savedProduct.getProductName());
        response.setCategory(savedProduct.getCategory());
        response.setPrice(savedProduct.getPrice());
        response.setWarrantyType(savedProduct.getWarrantyType());
        response.setDescription(savedProduct.getDescription());
        response.setManufacturerId(savedProduct.getManufacturerId());
        response.setMessage("Product created successfully");

        return response;
    }

    // ========== 2. GET ALL PRODUCTS BY MANUFACTURER ==========
    @Override
    public List<ProductResponse> getProductsByManufacturerId(int manufacturerId) {

        // Check if manufacturer exists
        if (!manufacturerMasterRepository.existsByManufacturerId(manufacturerId)) {
            throw new RuntimeException("Manufacturer not found with ID: " + manufacturerId);
        }

        // Get all products
        List<Product> products = productRepository.findByManufacturerId(manufacturerId);

        // Convert to response list
        return products.stream().map(product -> {
            ProductResponse response = new ProductResponse();
            response.setProductId(product.getProductId());
            response.setProductName(product.getProductName());
            response.setCategory(product.getCategory());
            response.setPrice(product.getPrice());
            response.setWarrantyType(product.getWarrantyType());
            response.setDescription(product.getDescription());
            response.setManufacturerId(product.getManufacturerId());
            return response;
        }).collect(Collectors.toList());
    }

    // ========== 3. GET SINGLE PRODUCT BY ID ==========
    @Override
    public ProductResponse getProductById(int productId, int manufacturerId) {

        // Check if manufacturer exists
        if (!manufacturerMasterRepository.existsByManufacturerId(manufacturerId)) {
            throw new RuntimeException("Manufacturer not found with ID: " + manufacturerId);
        }

        // Find product (FIXED: use correct repository method)
        Product product = productRepository.findByProductIdAndManufacturerId(productId, manufacturerId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // Prepare response
        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setCategory(product.getCategory());
        response.setPrice(product.getPrice());
        response.setWarrantyType(product.getWarrantyType());
        response.setDescription(product.getDescription());
        response.setManufacturerId(product.getManufacturerId());

        return response;
    }

    // ========== 4. UPDATE PRODUCT ==========
    @Override
    public ProductResponse updateProduct(int productId, ProductPayload payload) {

        // Find product by ID and manufacturer ID
        Product product = productRepository
                .findByProductIdAndManufacturerId(productId, payload.getManufacturerId().intValue())
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // Update fields
        product.setProductName(payload.getProductName());
        product.setCategory(payload.getCategory());
        product.setPrice(payload.getPrice());
        product.setWarrantyType(payload.getWarrantyType());
        product.setDescription(payload.getDescription());

        // SAVE the updated product
        Product updatedProduct = productRepository.save(product);

        // Prepare response
        ProductResponse response = new ProductResponse();
        response.setProductId(updatedProduct.getProductId());
        response.setProductName(updatedProduct.getProductName());
        response.setCategory(updatedProduct.getCategory());
        response.setPrice(updatedProduct.getPrice());
        response.setWarrantyType(updatedProduct.getWarrantyType());
        response.setDescription(updatedProduct.getDescription());
        response.setManufacturerId(updatedProduct.getManufacturerId());
        response.setMessage("Product updated successfully");

        return response;
    }
}