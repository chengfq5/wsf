package com.wsf.demo.provider.service.impl;

import com.wsf.demo.service.UserService;

public class UserServiceImpl implements UserService {
    @java.lang.Override
    public boolean existsMobileNo(String mobileNo) {
        return "18601720063".equals(mobileNo);
    }
}
