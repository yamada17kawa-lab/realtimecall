package com.nuliyang.controller;


import com.nuliyang.JwtUtil;
import com.nuliyang.dto.UserUpdateDto;
import com.nuliyang.entity.FriendEntity;
import com.nuliyang.entity.UserEntity;
import com.nuliyang.result.Result;
import com.nuliyang.service.UserService;
import com.nuliyang.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;


    /**
     * 添加用户
     */
    @PostMapping("/adduser")
    public Result<String> addUser(@RequestBody UserEntity userEntity) {
        userService.adduser(userEntity);
        return Result.success("添加用户成功");
    }


    /**
     * 根据用户名查询用户
     */
    @GetMapping("/getuser/{username}")
    public Result<UserEntity> getUserByUsername(@PathVariable String username) {
        return Result.success("查询用户成功", userService.getUserByUsername(username));
    }


    /**
     * 更新用户信息
     */
    @PostMapping("/updateUser")
    public Result<UserVo> updateUser(@RequestBody UserUpdateDto userUpdateDto,
                                     HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());
            userUpdateDto.setUserId(userId);
            return Result.success("更新用户成功", userService.updateUser(userUpdateDto));
        }
        return Result.error("缺少用户id");
    }


    /**
     * 注销账号
     */
    @GetMapping("/delete")
    public Result<String> deleteUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());
            userService.deleteUser(userId);
            return Result.success("注销成功");
        }
        return Result.error("注销失败");
    }


    /**
     * 添加好友
     */
    @PostMapping("/friend/add/{friendId}")
    public Result<String> addFriend(@PathVariable Long friendId,
                                    HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());
            FriendEntity friendEntity = new FriendEntity().setUserId(userId).setFriendId(friendId);
            userService.addFriend(friendEntity);
            return Result.success("好友添加成功");
        }
        return Result.error("好友添加失败");
    }


    /**
     * 删除好友
     */
    @PostMapping("/friend/delete/{friendId}")
    public Result<String> deleteFriend(@PathVariable Long friendId,
                                       HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());
            userService.deleteFriend(userId, friendId);
            return Result.success("好友删除成功");
        }
        return Result.error("好友删除失败");
    }


    /**
     * 获取好友列表
     */
    @GetMapping("/friend/list")
    public Result<List<UserVo>> getFriendList(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());
            return Result.success("获取好友列表成功", userService.getFriendList(userId));
        }

        return Result.error("获取好友列表失败");

    }



}
