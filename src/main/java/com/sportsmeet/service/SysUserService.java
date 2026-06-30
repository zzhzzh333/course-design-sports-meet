package com.sportsmeet.service;

import com.sportsmeet.entity.SysUser;

public interface SysUserService {
    SysUser login(String username, String password);
}