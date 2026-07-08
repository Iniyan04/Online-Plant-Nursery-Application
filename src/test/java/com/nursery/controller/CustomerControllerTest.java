package com.nursery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nursery.dto.LoginRequest;
import com.nursery.dto.RegisterRequest;
import com.nursery.dto.UpdateCustomerRequest;
import com.nursery.entity.Address;
import com.nursery.entity.Customer;
import com.nursery.exception.DuplicateUsernameException;
import com.nursery.exception.InvalidCredentialsException;
import com.nursery.service.IAdminService;
import com.nursery.service.ICustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller-layer tests for CustomerController (US-001 Login, US-002 Registration).
 * The service layer is mocked - these tests verify ONLY the HTTP wiring:
 * routing, status codes, and JSON request/response shape.
 */
@WebMvcTest(CustomerController.class)
@DisplayName("CustomerController (HTTP layer)")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICustomerService customerService;

    @MockBean
    private IAdminService adminService;

    @Test
    @DisplayName("POST /api/customers/login returns 200 and customer data on valid credentials")
    void login_validCredentials_returns200() throws Exception {
        Address address = new Address("12", "Green Park", "Bengaluru", "Karnataka", 560001);
        Customer customer = new Customer("Jane Doe", "jane@example.com", "janedoe", "secret123", address);
        customer.setCustomerId(1);

        when(customerService.validateCustomer("janedoe", "secret123")).thenReturn(customer);

        LoginRequest request = new LoginRequest("janedoe", "secret123");

        mockMvc.perform(post("/api/customers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("janedoe"))
                .andExpect(jsonPath("$.customerName").value("Jane Doe"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/customers/login returns 401 on invalid credentials")
    void login_invalidCredentials_returns401() throws Exception {
        when(customerService.validateCustomer(anyString(), anyString()))
                .thenThrow(new InvalidCredentialsException("Invalid username or password"));

        LoginRequest request = new LoginRequest("janedoe", "wrongpass");

        mockMvc.perform(post("/api/customers/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @DisplayName("POST /api/customers/register returns 201 and saved customer on valid data")
    void register_validData_returns201() throws Exception {
        Address address = new Address("45", "Lake View", "Chennai", "Tamil Nadu", 600001);
        Customer saved = new Customer("Alex Roy", "alex@example.com", "alexroy", "pass456", address);
        saved.setCustomerId(2);

        when(customerService.addCustomer(any(Customer.class))).thenReturn(saved);

        RegisterRequest request = new RegisterRequest();
        request.setCustomerName("Alex Roy");
        request.setCustomerEmail("alex@example.com");
        request.setUsername("alexroy");
        request.setPassword("pass456");
        request.setHouseNo("45");
        request.setColony("Lake View");
        request.setCity("Chennai");
        request.setState("Tamil Nadu");
        request.setPincode(600001);

        mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("alexroy"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @DisplayName("POST /api/customers/register returns 409 when username already exists")
    void register_duplicateUsername_returns409() throws Exception {
        when(customerService.addCustomer(any(Customer.class)))
                .thenThrow(new DuplicateUsernameException("Username 'janedoe' is already taken"));

        RegisterRequest request = new RegisterRequest();
        request.setCustomerName("Jane Doe");
        request.setCustomerEmail("jane@example.com");
        request.setUsername("janedoe");
        request.setPassword("secret123");

        mockMvc.perform(post("/api/customers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /api/customers returns 200 with all customers for valid admin")
    void viewAllCustomers_validAdmin_returns200() throws Exception {
        Address address = new Address("12", "Green Park", "Bengaluru", "Karnataka", 560001);
        Customer customer = new Customer("Jane Doe", "jane@example.com", "janedoe", "secret123", address);
        customer.setCustomerId(1);
        when(customerService.viewAllCustomers()).thenReturn(java.util.List.of(customer));

        mockMvc.perform(get("/api/customers")
                        .header("adminUsername", "admin")
                        .header("adminPassword", "admin123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].username").value("janedoe"));
    }

    @Test
    @DisplayName("GET /api/customers/{id} returns 200 for valid admin")
    void viewCustomerById_validAdmin_returns200() throws Exception {
        Address address = new Address("12", "Green Park", "Bengaluru", "Karnataka", 560001);
        Customer customer = new Customer("Jane Doe", "jane@example.com", "janedoe", "secret123", address);
        customer.setCustomerId(1);
        when(customerService.viewCustomer(1)).thenReturn(customer);

        mockMvc.perform(get("/api/customers/1")
                        .header("adminUsername", "admin")
                        .header("adminPassword", "admin123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1));
    }

    @Test
    @DisplayName("GET /api/customers/search/{username} returns 200 for valid admin")
    void viewCustomerByUsername_validAdmin_returns200() throws Exception {
        Address address = new Address("12", "Green Park", "Bengaluru", "Karnataka", 560001);
        Customer customer = new Customer("Jane Doe", "jane@example.com", "janedoe", "secret123", address);
        customer.setCustomerId(1);
        when(customerService.viewCustomer("janedoe")).thenReturn(customer);

        mockMvc.perform(get("/api/customers/search/janedoe")
                        .header("adminUsername", "admin")
                        .header("adminPassword", "admin123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("janedoe"));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} returns 200 when update succeeds for valid admin")
    void updateCustomer_validAdmin_returns200() throws Exception {
        Address address = new Address("12", "Green Park", "Bengaluru", "Karnataka", 560001);
        Customer existing = new Customer("Jane Doe", "jane@example.com", "janedoe", "secret123", address);
        existing.setCustomerId(1);
        Customer updated = new Customer("Jane Updated", "updated@example.com", "janedoe", "secret123", address);
        updated.setCustomerId(1);
        when(customerService.viewCustomer(1)).thenReturn(existing);
        when(customerService.updateCustomer(any(Customer.class))).thenReturn(updated);

        UpdateCustomerRequest request = new UpdateCustomerRequest();
        request.setCustomerName("Jane Updated");
        request.setCustomerEmail("updated@example.com");
        request.setUsername("janedoe");

        mockMvc.perform(put("/api/customers/1")
                        .header("adminUsername", "admin")
                        .header("adminPassword", "admin123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("Jane Updated"));
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} returns 204 when delete succeeds for valid admin")
    void deleteCustomer_validAdmin_returns204() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(1);
        when(customerService.deleteCustomer(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(delete("/api/customers/1")
                        .header("adminUsername", "admin")
                        .header("adminPassword", "admin123"))
                .andExpect(status().isNoContent());
    }
}
