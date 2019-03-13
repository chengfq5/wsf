package com.wsf.demo.service;


public interface UserService {

    /**
     * 判断用户手机号是否存在
     *
     * @param mobileNo
     * @return
     */
    boolean existsMobileNo(String mobileNo);

}
