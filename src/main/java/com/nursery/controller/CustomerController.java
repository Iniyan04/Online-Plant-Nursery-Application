package com.nursery.controller;

import com.nursery.dto.CustomerResponse;
import com.nursery.dto.LoginRequest;
import com.nursery.dto.RegisterRequest;
import com.nursery.entity.Address;
import com.nursery.entity.Customer;
import com.nursery.service.ICustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST endpoints for US-001 (Login) and US-002 (Customer Registration).
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * US-001: User Login.
     * POST /api/customers/login
     */
    @PostMapping("/login")
    public ResponseEntity<CustomerResponse> login(@RequestBody LoginRequest request) {
        Customer customer = customerService.validateCustomer(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(CustomerResponse.fromEntity(customer));
    }

    /**
     * US-002: Customer Registration.
     * POST /api/customers/register
     */
    @PostMapping("/register")
    public ResponseEntity<CustomerResponse> register(@RequestBody RegisterRequest request) {
        Address address = new Address(
                request.getHouseNo(),
                request.getColony(),
                request.getCity(),
                request.getState(),
                request.getPincode()
        );

        Customer newCustomer = new Customer(
                request.getCustomerName(),
                request.getCustomerEmail(),
                request.getUsername(),
                request.getPassword(),
                address
        );

        Customer saved = customerService.addCustomer(newCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerResponse.fromEntity(saved));
    }
}
