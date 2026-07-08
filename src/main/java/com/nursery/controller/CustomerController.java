package com.nursery.controller;

import com.nursery.dto.CustomerResponse;
import com.nursery.dto.LoginRequest;
import com.nursery.dto.RegisterRequest;
import com.nursery.dto.UpdateCustomerRequest;
import com.nursery.entity.Address;
import com.nursery.entity.Customer;
import com.nursery.service.IAdminService;
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
    private final IAdminService adminService;

    public CustomerController(ICustomerService customerService, IAdminService adminService) {
        this.customerService = customerService;
        this.adminService = adminService;
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

    @GetMapping
    public ResponseEntity<java.util.List<CustomerResponse>> viewAllCustomers(
            @RequestHeader("adminUsername") String adminUsername,
            @RequestHeader("adminPassword") String adminPassword) {
        authorizeAdmin(adminUsername, adminPassword);
        java.util.List<CustomerResponse> customers = customerService.viewAllCustomers()
                .stream()
                .map(CustomerResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> viewCustomerById(
            @RequestHeader("adminUsername") String adminUsername,
            @RequestHeader("adminPassword") String adminPassword,
            @PathVariable int customerId) {
        authorizeAdmin(adminUsername, adminPassword);
        return ResponseEntity.ok(CustomerResponse.fromEntity(customerService.viewCustomer(customerId)));
    }

    @GetMapping("/search/{username}")
    public ResponseEntity<CustomerResponse> viewCustomerByUsername(
            @RequestHeader("adminUsername") String adminUsername,
            @RequestHeader("adminPassword") String adminPassword,
            @PathVariable String username) {
        authorizeAdmin(adminUsername, adminPassword);
        return ResponseEntity.ok(CustomerResponse.fromEntity(customerService.viewCustomer(username)));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @RequestHeader("adminUsername") String adminUsername,
            @RequestHeader("adminPassword") String adminPassword,
            @PathVariable int customerId,
            @RequestBody UpdateCustomerRequest request) {
        authorizeAdmin(adminUsername, adminPassword);

        Customer existing = customerService.viewCustomer(customerId);
        Customer updatedCustomer = new Customer(
                request.getCustomerName(),
                request.getCustomerEmail(),
                request.getUsername(),
                existing.getPassword(),
                existing.getAddress()
        );
        updatedCustomer.setCustomerId(customerId);

        Customer updated = customerService.updateCustomer(updatedCustomer);
        return ResponseEntity.ok(CustomerResponse.fromEntity(updated));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(
            @RequestHeader("adminUsername") String adminUsername,
            @RequestHeader("adminPassword") String adminPassword,
            @PathVariable int customerId) {
        authorizeAdmin(adminUsername, adminPassword);

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customerService.deleteCustomer(customer);
        return ResponseEntity.noContent().build();
    }

    private void authorizeAdmin(String adminUsername, String adminPassword) {
        adminService.validateAdmin(adminUsername, adminPassword);
    }
}
