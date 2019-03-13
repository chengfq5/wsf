package com.wsf.demo.provider.service.impl;

import com.wsf.demo.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
