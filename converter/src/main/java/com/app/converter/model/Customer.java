package com.app.converter.model;

import com.app.converter.exception.CustomerBuildingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Customer {

    private String name;
    private String surname;
    private int age;
    private String email;

    public static CustomerBuilder builder(){
        return new CustomCustomerBuilder();
    }

    private static class CustomCustomerBuilder extends CustomerBuilder {
        @Override
        public Customer build() throws CustomerBuildingException {
            if(!super.name.matches("[A-Z]+( [A-Z]+)+?")){
                throw new CustomerBuildingException("Incorrect name value");
            }
            if(!super.surname.matches("[A-Z]+( [A-Z]+)+?")){
                throw new CustomerBuildingException("Incorrect surname value");
            }
            if(super.age < 18){
                throw new CustomerBuildingException("Customer age can not be less than 18");
            }
            if(!super.email.matches("[a-zA-Z!#$%&'*+\\-/=?^_`{|}.~\\d]+@[A-Za-z\\d]+\\.[a-zA-Z]{2,3}")){
                throw new CustomerBuildingException("Incorrect email address format");
            }
            return super.build();
        }
    }
}
