package com.nursery.controller;

import com.nursery.dto.AdminResponse;
import com.nursery.dto.DashboardResponse;
import com.nursery.dto.LoginRequest;
import com.nursery.entity.Admin;
import com.nursery.service.IAdminDashboardService;
import com.nursery.service.IAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final IAdminService adminService;
    private final IAdminDashboardService adminDashboardService;

    public AdminController(IAdminService adminService, IAdminDashboardService adminDashboardService) {
        this.adminService = adminService;
        this.adminDashboardService = adminDashboardService;
    }

    @PostMapping("/login")
    public ResponseEntity<AdminResponse> login(@RequestBody LoginRequest request) {
        Admin admin = adminService.validateAdmin(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(AdminResponse.fromEntity(admin));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestHeader("adminUsername") String adminUsername,
            @RequestHeader("adminPassword") String adminPassword) {
        adminService.validateAdmin(adminUsername, adminPassword);
        return ResponseEntity.ok(adminDashboardService.getDashboardData());
    }
}
