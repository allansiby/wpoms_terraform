package com.wpoms.admin.models.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private int productId;
    private String productName;
    private String category;
    private Double price;
    private String warrantyType;
    private String description;
    private int manufacturerId;
    private String message;

}
