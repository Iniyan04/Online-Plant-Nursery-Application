package com.nursery.service;

import com.nursery.entity.Customer;
import com.nursery.exception.DuplicateUsernameException;
import com.nursery.exception.InvalidCredentialsException;
import com.nursery.exception.InvalidCustomerDataException;
import com.nursery.repository.ICustomerRepository;

import java.util.List;
import java.util.regex.Pattern;

public class CustomerServiceImpl implements ICustomerService {

    // Basic email shape check: something@something.something
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final ICustomerRepository customerRepository;

    public CustomerServiceImpl(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer addCustomer(Customer customer) {
        validateRegistrationData(customer);

        if (customerRepository.findByUsername(customer.getUsername()) != null) {
            throw new DuplicateUsernameException(
                    "Username '" + customer.getUsername() + "' is already taken");
        }

        return customerRepository.addCustomer(customer);
    }

    private void validateRegistrationData(Customer customer) {
        if (customer == null) {
            throw new InvalidCustomerDataException("Customer data must not be null");
        }
        if (isBlank(customer.getCustomerName())) {
            throw new InvalidCustomerDataException("Customer name must not be empty");
        }
        if (isBlank(customer.getUsername())) {
            throw new InvalidCustomerDataException("Username must not be empty");
        }
        if (isBlank(customer.getPassword())) {
            throw new InvalidCustomerDataException("Password must not be empty");
        }
        if (isBlank(customer.getCustomerEmail()) || !EMAIL_PATTERN.matcher(customer.getCustomerEmail()).matches()) {
            throw new InvalidCustomerDataException("Email must be a valid email address");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        validateCustomerManagementData(customer);

        Customer existing = customerRepository.viewCustomer(customer.getCustomerId());
        if (existing == null) {
            throw new InvalidCustomerDataException("No customer found with ID " + customer.getCustomerId());
        }

        Customer duplicate = customerRepository.findByUsername(customer.getUsername());
        if (duplicate != null && duplicate.getCustomerId() != customer.getCustomerId()) {
            throw new DuplicateUsernameException(
                    "Username '" + customer.getUsername() + "' is already taken");
        }

        if (isBlank(customer.getPassword())) {
            customer.setPassword(existing.getPassword());
        }

        return customerRepository.updateCustomer(customer);
    }

    @Override
    public Customer deleteCustomer(Customer customer) {
        if (customer == null || customer.getCustomerId() <= 0) {
            throw new InvalidCustomerDataException("Customer ID must be greater than zero");
        }

        Customer existing = customerRepository.viewCustomer(customer.getCustomerId());
        if (existing == null) {
            throw new InvalidCustomerDataException("No customer found with ID " + customer.getCustomerId());
        }

        return customerRepository.deleteCustomer(customer);
    }

    @Override
    public Customer viewCustomer(int customerId) {
        if (customerId <= 0) {
            throw new InvalidCustomerDataException("Customer ID must be greater than zero");
        }

        Customer customer = customerRepository.viewCustomer(customerId);
        if (customer == null) {
            throw new InvalidCustomerDataException("No customer found with ID " + customerId);
        }
        return customer;
    }

    @Override
    public List<Customer> viewAllCustomers() {
        return customerRepository.viewAllCustomers();
    }

    @Override
    public Customer viewCustomer(String username) {
        if (isBlank(username)) {
            throw new InvalidCustomerDataException("Username must not be empty");
        }

        Customer customer = customerRepository.findByUsername(username);
        if (customer == null) {
            throw new InvalidCustomerDataException("No customer found with username '" + username + "'");
        }
        return customer;
    }

    @Override
    public Customer validateCustomer(String userName, String password) {
        if (userName == null || userName.isBlank() || password == null || password.isBlank()) {
            throw new InvalidCredentialsException("Username and password must not be empty");
        }

        Customer customer = customerRepository.validateCustomer(userName, password);

        if (customer == null) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return customer;
    }

    private void validateCustomerManagementData(Customer customer) {
        if (customer == null) {
            throw new InvalidCustomerDataException("Customer data must not be null");
        }
        if (customer.getCustomerId() <= 0) {
            throw new InvalidCustomerDataException("Customer ID must be greater than zero");
        }
        if (isBlank(customer.getCustomerName())) {
            throw new InvalidCustomerDataException("Customer name must not be empty");
        }
        if (isBlank(customer.getUsername())) {
            throw new InvalidCustomerDataException("Username must not be empty");
        }
        if (isBlank(customer.getCustomerEmail()) || !EMAIL_PATTERN.matcher(customer.getCustomerEmail()).matches()) {
            throw new InvalidCustomerDataException("Email must be a valid email address");
        }
    }
}
