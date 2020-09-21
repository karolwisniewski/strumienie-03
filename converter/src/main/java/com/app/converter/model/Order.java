package com.app.converter.model;

import com.app.converter.exception.OrderBuildingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Order {

    private Customer customer;
    private Product product;
    private double quantity;
    private LocalDate orderDate;


    private static class CustomOrderBuilder extends OrderBuilder{
        @Override
        public Order build() throws OrderBuildingException{
            if(Objects.isNull(super.customer)){
                throw new OrderBuildingException("Customer object is null!");
            }
            if(Objects.isNull(super.product)){
                throw new OrderBuildingException("Product object is null!");
            }
            if(super.quantity <=0){
                throw new OrderBuildingException("Quantity can not be equal or less than zero");
            }
            if(super.orderDate.isBefore(LocalDate.now())){
                throw new OrderBuildingException("Order date can not be earlier than today");
            }
            return super.build();
        }
    }
}
