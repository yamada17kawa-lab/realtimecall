package com.nuliyang.feign;

import com.nuliyang.entity.UserEntity;
import com.nuliyang.result.Result;
import com.nuliyang.vo.UserVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "user-service")
public interface UserFeign {


    /**
     * 添加用户
     */
    @PostMapping("/adduser")
    Result<UserVo> addUser(@RequestBody UserEntity userEntity);

    /**
     * 根据用户名查询用户
     */
    @GetMapping("/getuser/{username}")
    Result<UserEntity> getUserByUsername(@PathVariable String username);


}
