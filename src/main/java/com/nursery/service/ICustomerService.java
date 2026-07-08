package com.nursery.service;

import com.nursery.entity.Customer;
import java.util.List;

public interface ICustomerService {

    Customer addCustomer(Customer customer);

    Customer updateCustomer(Customer customer);

    Customer deleteCustomer(Customer customer);

    Customer viewCustomer(int customerId);

    List<Customer> viewAllCustomers();

    Customer viewCustomer(String username);

    /**
     * Validates login credentials.
     *
     * @return the matching Customer if credentials are valid
     * @throws com.nursery.exception.InvalidCredentialsException if the username
     *         does not exist or the password does not match
     */
    Customer validateCustomer(String userName, String password);
}
