package com.nursery.service;

import com.nursery.entity.Admin;
import com.nursery.exception.InvalidCredentialsException;
import com.nursery.repository.IAdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminServiceImpl")
class AdminServiceImplTest {

    @Mock
    private IAdminRepository adminRepository;

    private IAdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminServiceImpl(adminRepository);
    }

    @Test
    @DisplayName("validateAdmin returns admin when credentials are correct")
    void validateAdmin_validCredentials_returnsAdmin() {
        Admin admin = new Admin("admin", "admin123");
        when(adminRepository.validateAdmin("admin", "admin123")).thenReturn(admin);

        Admin result = adminService.validateAdmin("admin", "admin123");

        assertNotNull(result);
        assertEquals("admin", result.getAdminUsername());
    }

    @Test
    @DisplayName("validateAdmin throws InvalidCredentialsException when username is blank")
    void validateAdmin_blankUsername_throwsException() {
        assertThrows(InvalidCredentialsException.class,
                () -> adminService.validateAdmin(" ", "admin123"));
        verify(adminRepository, never()).validateAdmin(" ", "admin123");
    }

    @Test
    @DisplayName("validateAdmin throws InvalidCredentialsException when credentials do not match")
    void validateAdmin_invalidCredentials_throwsException() {
        when(adminRepository.validateAdmin("admin", "wrongpass")).thenReturn(null);

        assertThrows(InvalidCredentialsException.class,
                () -> adminService.validateAdmin("admin", "wrongpass"));
    }

    @Test
    @DisplayName("ensureDefaultAdmin creates the default admin when it is missing")
    void ensureDefaultAdmin_missingAdmin_createsDefaultAdmin() {
        when(adminRepository.findByUsername(AdminServiceImpl.DEFAULT_ADMIN_USERNAME)).thenReturn(null);

        adminService.ensureDefaultAdmin();

        verify(adminRepository).addAdmin(org.mockito.ArgumentMatchers.any(Admin.class));
    }
}
