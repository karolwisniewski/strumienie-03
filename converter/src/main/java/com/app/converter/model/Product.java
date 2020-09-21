package com.app.converter.model;

import com.app.converter.exception.ProductBuildingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Product {
    private String name;
    private BigDecimal price;
    private Category category;
    
}
