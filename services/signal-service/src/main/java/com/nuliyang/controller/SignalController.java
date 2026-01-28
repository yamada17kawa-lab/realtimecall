package com.nuliyang.controller;

import com.nuliyang.result.Result;
import com.nuliyang.service.SignalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignalController {

    private final SignalService signalService;

    /**
     * 获取用户在线状态
     * @param userId 用户ID
     * @return 在线状态
     */
    //TODO 获取用户在线状态
    @GetMapping("/online-status/{userId}")
    public Result getOnlineStatus(@PathVariable Long userId) {
        return null;
    }


    /**
     * 用户连接
     */
    //TODO 用户连接
    @PostMapping("/session/connect")
    public Result connect() {
        return null;
    }



    /**
     * 用户断开连接
     */
    //TODO 用户断开连接
    @PostMapping("/session/disconnect")
    public Result disconnect() {
        return null;
    }


}
