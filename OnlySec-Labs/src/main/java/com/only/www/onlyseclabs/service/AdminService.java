package com.only.www.onlyseclabs.service;

import com.only.www.onlyseclabs.entity.Admin;

public interface AdminService {
    Admin getUserByLogin(String username, String password);
}