package com.nuliyang.service;

import com.nuliyang.dto.LoginDto;
import com.nuliyang.dto.RegisterDto;
import com.nuliyang.vo.UserVo;

public interface AuthService {

    /**
     * 注册
     */
    UserVo register(RegisterDto registerDto);


    /**
     * 登录
     */
    UserVo login(LoginDto loginDto);
}
