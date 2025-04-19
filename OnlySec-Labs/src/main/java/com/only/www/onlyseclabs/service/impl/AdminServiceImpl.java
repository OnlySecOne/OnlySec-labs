package com.only.www.onlyseclabs.service.impl;

import com.only.www.onlyseclabs.entity.Admin;
import com.only.www.onlyseclabs.mapper.AdminMapper;
import com.only.www.onlyseclabs.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin getUserByLogin(String username, String password) {
        return adminMapper.findByLogin(username, password);
    }
}