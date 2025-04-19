package com.only.www.onlyseclabs.service.impl;

import com.only.www.onlyseclabs.entity.Admin;
import com.only.www.onlyseclabs.mapper.CaptchaMapper;
import com.only.www.onlyseclabs.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    @Autowired
    private CaptchaMapper captchaMapper;

    public Admin getCaptcha(String username, String password) {
        return captchaMapper.findByUsernameAndPassword(username,password);
    }
}
