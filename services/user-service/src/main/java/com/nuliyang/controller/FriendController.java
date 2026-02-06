package com.nuliyang.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nuliyang.JwtUtil;
import com.nuliyang.result.PageResult;
import com.nuliyang.result.Result;
import com.nuliyang.service.FriendService;
import com.nuliyang.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FriendController {



    private final FriendService friendService;






    /**
     * 申请好友
     */
    @GetMapping("/friend/apply/{friendId}")
    public Result<String> applyFriend(@PathVariable Long friendId,
                                      HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());
            friendService.applyFriend(userId, friendId);
            return Result.success("好友申请成功");
        }
        return Result.error("好友申请失败");
    }


    /**
     * 根据token获取好友申请列表
     */
    @GetMapping("/getApply")
    public Result<PageResult<UserVo>> getApply(HttpServletRequest request,
                                               @RequestParam(defaultValue = "1") long current,
                                               @RequestParam(defaultValue = "9") long size) throws JsonProcessingException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());
            log.info("用户id: {}", userId);
            IPage<UserVo> page = friendService.getApply(userId, current, size);
            PageResult<UserVo> result = new PageResult<>(
                    page.getRecords(),
                    page.getTotal(),
                    page.getPages()
            );
            log.info("好友申请列表数据: {}",  result);
            return Result.success("获取好友申请数据成功", result);
        }


        return Result.error("获取好友申请数据失败");
    }


    /**
     * 删除好友申请
     */
    @GetMapping("/apply/delete/{userId}")
    public Result<String> deleteApply(@PathVariable Long userId) {
        friendService.deleteApply(userId);
        return Result.success("删除好友申请成功");
    }
}
