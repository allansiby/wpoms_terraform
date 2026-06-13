package com.wpoms.admin.services;

import java.util.List;

import com.wpoms.admin.models.payloads.ProductPayload;
import com.wpoms.admin.models.response.ProductResponse;

public interface IProductService {

    // Create new product
    ProductResponse createProduct(ProductPayload payload);

    // Get all products by manufacturer ID
    List<ProductResponse> getProductsByManufacturerId(int manufacturerId);

    // Get single product by ID
    ProductResponse getProductById(int productId, int manufacturerId);

    // Update product
    ProductResponse updateProduct(int productId, ProductPayload payload);

}