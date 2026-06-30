package com.sportsmeet.mapper;

import com.sportsmeet.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserMapper {
    SysUser findByUsername(String username);
    int insert(SysUser user);
}