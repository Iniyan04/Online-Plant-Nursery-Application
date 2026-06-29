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
        return customerRepository.updateCustomer(customer);
    }

    @Override
    public Customer deleteCustomer(Customer customer) {
        return customerRepository.deleteCustomer(customer);
    }

    @Override
    public Customer viewCustomer(int customerId) {
        return customerRepository.viewCustomer(customerId);
    }

    @Override
    public List<Customer> viewAllCustomers() {
        return customerRepository.viewAllCustomers();
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
}
