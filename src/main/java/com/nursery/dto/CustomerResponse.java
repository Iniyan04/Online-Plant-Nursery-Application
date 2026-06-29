package com.nursery.dto;

import com.nursery.entity.Customer;

/**
 * Response body representing a Customer WITHOUT the password field -
 * used for login responses and customer listings so passwords are
 * never sent back over the API.
 */
public class CustomerResponse {

    private int customerId;
    private String customerName;
    private String customerEmail;
    private String username;

    public CustomerResponse() {
    }

    public CustomerResponse(int customerId, String customerName, String customerEmail, String username) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.username = username;
    }

    public static CustomerResponse fromEntity(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getCustomerEmail(),
                customer.getUsername()
        );
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
