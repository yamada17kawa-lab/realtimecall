package com.nuliyang.controller;


import com.nuliyang.JwtUtil;
import com.nuliyang.dto.LoginDto;
import com.nuliyang.dto.RegisterDto;
import com.nuliyang.result.Result;
import com.nuliyang.service.AuthService;
import com.nuliyang.vo.UserVo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;



    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDto registerDto,
                                   HttpServletResponse response) {
        UserVo userVo = authService.register(registerDto);
        log.info("用户注册成功：{}", userVo);
        String token = JwtUtil.generateToken(userVo.getId());
        response.setHeader("Authorization", "Bearer " + token);
        return Result.success("注册成功", userVo);

    }


    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDto loginDto,
                                HttpServletResponse response) {
        log.info("用户登录：{}", loginDto);
        UserVo userVo = authService.login(loginDto);
        log.info("用户登录成功：{}", userVo);
        String token = JwtUtil.generateToken(userVo.getId());
        response.setHeader("Authorization", "Bearer " + token);
        return Result.success("登录成功", userVo);
    }

}
