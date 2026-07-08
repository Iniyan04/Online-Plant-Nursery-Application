package com.nursery.service;

import com.nursery.entity.Admin;

public interface IAdminService {

    Admin validateAdmin(String userName, String password);

    void ensureDefaultAdmin();
}
