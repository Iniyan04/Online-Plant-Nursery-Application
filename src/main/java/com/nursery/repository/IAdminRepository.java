package com.nursery.repository;

import com.nursery.entity.Admin;

public interface IAdminRepository {

    Admin addAdmin(Admin admin);

    Admin validateAdmin(String userName, String password);

    Admin findByUsername(String userName);
}
