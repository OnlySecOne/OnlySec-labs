package com.only.www.onlyseclabs.service;

import com.only.www.onlyseclabs.entity.Admin;

public interface CaptchaService {
    public Admin getCaptcha(String username, String password);
}
