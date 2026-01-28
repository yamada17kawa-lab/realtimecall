package com.nuliyang.service.serviceimpl;

import com.nuliyang.BizException;
import com.nuliyang.ErrorCode;
import com.nuliyang.feign.UserFeign;
import com.nuliyang.dto.LoginDto;
import com.nuliyang.dto.RegisterDto;
import com.nuliyang.entity.UserEntity;
import com.nuliyang.result.Result;
import com.nuliyang.service.AuthService;
import com.nuliyang.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
public class AuthServiceImpl implements AuthService {


    @Autowired
    UserFeign userFeign;


    /**
     * 注册
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserVo register(RegisterDto registerDto) {
        //先根据用户名从数据库中查出是否已存在

        UserEntity user = new UserEntity()
                .setPassword(registerDto.getPassword())
                .setUsername(registerDto.getUsername());


        //如果存在则返回错误
        if (userFeign.addUser(user).getCode().equals(500)) {
            throw new BizException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        //不存在添加该用户
        user.setPassword(BCrypt.hashpw(registerDto.getPassword(), BCrypt.gensalt()));
        userFeign.addUser(user);

        UserEntity userEntity = userFeign.getUserByUsername(user.getUsername()).getData();
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userEntity, userVo);

        return userVo;

    }


    /**
     * 登录
     */
    @Override
    public UserVo login(LoginDto loginDto) {
        //先查出用户
        Result<UserEntity> result = userFeign.getUserByUsername(loginDto.getUsername());
        //返回状态码为500则说明用户不存在
        if (result.getCode().equals(500)) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        //将用户密码与数据库对比
        //密码错误则返回错误
        if (!BCrypt.checkpw(loginDto.getPassword(), result.getData().getPassword())) {
            throw new BizException(ErrorCode.PASSWORD_ERROR);
        }
        UserVo userVo = new UserVo();
        log.info("UserEntity: {}", result.getData());
        BeanUtils.copyProperties(result.getData(), userVo);
        return userVo;

    }
}
