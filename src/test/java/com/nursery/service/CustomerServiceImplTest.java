package com.nursery.service;

import com.nursery.entity.Address;
import com.nursery.entity.Customer;
import com.nursery.exception.DuplicateUsernameException;
import com.nursery.exception.InvalidCredentialsException;
import com.nursery.exception.InvalidCustomerDataException;
import com.nursery.repository.ICustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerServiceImpl#validateCustomer (US-001: User Login).
 * The repository is mocked so this test exercises ONLY the service's
 * business logic, with no real database involved.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerServiceImpl - validateCustomer (US-001 Login)")
class CustomerServiceImplTest {

    @Mock
    private ICustomerRepository customerRepository;

    private ICustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository);
    }

    @Test
    @DisplayName("Returns the Customer when username and password are correct")
    void validateCustomer_withCorrectCredentials_returnsCustomer() {
        // Arrange
        String username = "janedoe";
        String password = "secret123";
        Customer expectedCustomer = new Customer("Jane Doe", "jane@example.com", username, password, null);

        when(customerRepository.validateCustomer(username, password)).thenReturn(expectedCustomer);

        // Act
        Customer result = customerService.validateCustomer(username, password);

        // Assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals("Jane Doe", result.getCustomerName());
        verify(customerRepository, times(1)).validateCustomer(username, password);
    }

    @Test
    @DisplayName("Throws InvalidCredentialsException when password is wrong")
    void validateCustomer_withWrongPassword_throwsException() {
        // Arrange
        String username = "janedoe";
        String wrongPassword = "wrongpass";

        when(customerRepository.validateCustomer(username, wrongPassword)).thenReturn(null);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class,
                () -> customerService.validateCustomer(username, wrongPassword));
    }

    @Test
    @DisplayName("Throws InvalidCredentialsException when username does not exist")
    void validateCustomer_withUnknownUsername_throwsException() {
        // Arrange
        String unknownUsername = "ghost";
        String anyPassword = "whatever";

        when(customerRepository.validateCustomer(unknownUsername, anyPassword)).thenReturn(null);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class,
                () -> customerService.validateCustomer(unknownUsername, anyPassword));
    }

    @Test
    @DisplayName("Throws InvalidCredentialsException when username is null")
    void validateCustomer_withNullUsername_throwsException() {
        assertThrows(InvalidCredentialsException.class,
                () -> customerService.validateCustomer(null, "somepassword"));

        // Repository should never even be called with bad input
        verify(customerRepository, never()).validateCustomer(any(), any());
    }

    @Test
    @DisplayName("Throws InvalidCredentialsException when password is null")
    void validateCustomer_withNullPassword_throwsException() {
        assertThrows(InvalidCredentialsException.class,
                () -> customerService.validateCustomer("janedoe", null));

        verify(customerRepository, never()).validateCustomer(any(), any());
    }

    @Test
    @DisplayName("Throws InvalidCredentialsException when username is blank")
    void validateCustomer_withBlankUsername_throwsException() {
        assertThrows(InvalidCredentialsException.class,
                () -> customerService.validateCustomer("   ", "somepassword"));

        verify(customerRepository, never()).validateCustomer(any(), any());
    }

    // ----------------------------------------------------------------
    // US-002: Customer Registration (addCustomer)
    // ----------------------------------------------------------------

    private Customer validNewCustomer() {
        Address address = new Address("221B", "Baker Street", "Bengaluru", "Karnataka", 560001);
        return new Customer("Jane Doe", "jane@example.com", "janedoe", "secret123", address);
    }

    @Test
    @DisplayName("Registers successfully when all fields are valid and username is unique")
    void addCustomer_withValidData_returnsSavedCustomer() {
        Customer newCustomer = validNewCustomer();
        when(customerRepository.findByUsername("janedoe")).thenReturn(null);
        when(customerRepository.addCustomer(newCustomer)).thenReturn(newCustomer);

        Customer result = customerService.addCustomer(newCustomer);

        assertNotNull(result);
        assertEquals("janedoe", result.getUsername());
        verify(customerRepository, times(1)).addCustomer(newCustomer);
    }

    @Test
    @DisplayName("Throws DuplicateUsernameException when username already exists")
    void addCustomer_withExistingUsername_throwsException() {
        Customer newCustomer = validNewCustomer();
        when(customerRepository.findByUsername("janedoe")).thenReturn(validNewCustomer());

        assertThrows(DuplicateUsernameException.class,
                () -> customerService.addCustomer(newCustomer));

        verify(customerRepository, never()).addCustomer(any());
    }

    @Test
    @DisplayName("Throws InvalidCustomerDataException when customer name is blank")
    void addCustomer_withBlankName_throwsException() {
        Customer newCustomer = new Customer("  ", "jane@example.com", "janedoe", "secret123", null);

        assertThrows(InvalidCustomerDataException.class,
                () -> customerService.addCustomer(newCustomer));

        verify(customerRepository, never()).addCustomer(any());
    }

    @Test
    @DisplayName("Throws InvalidCustomerDataException when username is blank")
    void addCustomer_withBlankUsername_throwsException() {
        Customer newCustomer = new Customer("Jane Doe", "jane@example.com", " ", "secret123", null);

        assertThrows(InvalidCustomerDataException.class,
                () -> customerService.addCustomer(newCustomer));

        verify(customerRepository, never()).addCustomer(any());
    }

    @Test
    @DisplayName("Throws InvalidCustomerDataException when password is blank")
    void addCustomer_withBlankPassword_throwsException() {
        Customer newCustomer = new Customer("Jane Doe", "jane@example.com", "janedoe", "  ", null);

        assertThrows(InvalidCustomerDataException.class,
                () -> customerService.addCustomer(newCustomer));

        verify(customerRepository, never()).addCustomer(any());
    }

    @Test
    @DisplayName("Throws InvalidCustomerDataException when email is missing '@'")
    void addCustomer_withMalformedEmail_throwsException() {
        Customer newCustomer = new Customer("Jane Doe", "jane.example.com", "janedoe", "secret123", null);

        assertThrows(InvalidCustomerDataException.class,
                () -> customerService.addCustomer(newCustomer));

        verify(customerRepository, never()).addCustomer(any());
    }

    @Test
    @DisplayName("Throws InvalidCustomerDataException when email has no domain after '@'")
    void addCustomer_withEmailMissingDomain_throwsException() {
        Customer newCustomer = new Customer("Jane Doe", "jane@", "janedoe", "secret123", null);

        assertThrows(InvalidCustomerDataException.class,
                () -> customerService.addCustomer(newCustomer));

        verify(customerRepository, never()).addCustomer(any());
    }

    @Test
    @DisplayName("Throws InvalidCustomerDataException when customer object itself is null")
    void addCustomer_withNullCustomer_throwsException() {
        assertThrows(InvalidCustomerDataException.class,
                () -> customerService.addCustomer(null));

        verify(customerRepository, never()).addCustomer(any());
    }
}
