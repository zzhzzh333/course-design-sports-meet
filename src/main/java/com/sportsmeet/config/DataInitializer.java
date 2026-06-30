package com.sportsmeet.config;

import com.sportsmeet.entity.SysUser;
import com.sportsmeet.mapper.SysUserMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(SysUserMapper sysUserMapper) {
        return args -> {
            SysUser exist = sysUserMapper.findByUsername("admin");
            if (exist == null) {
                SysUser admin = new SysUser();
                admin.setUsername("admin");
                admin.setPassword(new BCryptPasswordEncoder().encode("123456"));
                admin.setRealName("系统管理员");
                sysUserMapper.insert(admin);
            }
        };
    }
}