package com.nursery.repository;

import com.nursery.entity.Customer;
import java.util.List;

public interface ICustomerRepository {

    Customer addCustomer(Customer customer);

    Customer updateCustomer(Customer customer);

    Customer deleteCustomer(Customer customer);

    Customer viewCustomer(int customerId);

    List<Customer> viewAllCustomers();

    Customer validateCustomer(String userName, String password);

    /**
     * Looks up a customer by username only (no password check).
     * Added beyond the original class diagram to support duplicate-username
     * checking during registration (US-002).
     */
    Customer findByUsername(String userName);
}
