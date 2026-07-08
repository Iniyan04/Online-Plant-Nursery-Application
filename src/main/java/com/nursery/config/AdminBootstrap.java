package com.nursery.config;

import com.nursery.service.IAdminService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap {

    private final IAdminService adminService;

    public AdminBootstrap(IAdminService adminService) {
        this.adminService = adminService;
    }

    @PostConstruct
    public void initializeDefaultAdmin() {
        adminService.ensureDefaultAdmin();
    }
}
