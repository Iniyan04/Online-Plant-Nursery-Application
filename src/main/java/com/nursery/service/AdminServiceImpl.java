package com.nursery.service;

import com.nursery.entity.Admin;
import com.nursery.exception.InvalidCredentialsException;
import com.nursery.repository.IAdminRepository;

public class AdminServiceImpl implements IAdminService {

    public static final String DEFAULT_ADMIN_USERNAME = "admin";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private final IAdminRepository adminRepository;

    public AdminServiceImpl(IAdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public Admin validateAdmin(String userName, String password) {
        if (isBlank(userName) || isBlank(password)) {
            throw new InvalidCredentialsException("Admin username and password must not be empty");
        }

        Admin admin = adminRepository.validateAdmin(userName, password);
        if (admin == null) {
            throw new InvalidCredentialsException("Invalid admin username or password");
        }
        return admin;
    }

    @Override
    public void ensureDefaultAdmin() {
        if (adminRepository.findByUsername(DEFAULT_ADMIN_USERNAME) == null) {
            adminRepository.addAdmin(new Admin(DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD));
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
