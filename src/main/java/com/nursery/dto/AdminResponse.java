package com.nursery.dto;

import com.nursery.entity.Admin;

public class AdminResponse {

    private int adminId;
    private String adminUsername;

    public AdminResponse() {
    }

    public AdminResponse(int adminId, String adminUsername) {
        this.adminId = adminId;
        this.adminUsername = adminUsername;
    }

    public static AdminResponse fromEntity(Admin admin) {
        return new AdminResponse(admin.getAdminId(), admin.getAdminUsername());
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }
}
