package com.nuliyang.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nuliyang.JwtUtil;
import com.nuliyang.dto.UserUpdateDto;
import com.nuliyang.entity.FriendEntity;
import com.nuliyang.entity.UserEntity;
import com.nuliyang.result.PageResult;
import com.nuliyang.result.Result;
import com.nuliyang.service.UserService;
import com.nuliyang.vo.UserVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;



    /**
     * 获取自己
     */
    @GetMapping("/myself")
    public Result<UserVo> getMyself(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());
            return Result.success("找到自己了", userService.getUserById(userId));
        }
        return Result.error("找不到自己");
    }


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
        log.info("进来控制层的参数为: {}", userUpdateDto.toString());
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());
            userUpdateDto.setUserId(userId);
            UserVo userVo = userService.updateUser(userUpdateDto);
            log.info("更新用户信息: {}", userVo);
            return Result.success("更新用户成功", userVo);
        }
        return Result.error("更新用户失败");
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
    @PostMapping("/friend/add/{userId}")
    public Result<String> addFriend(@PathVariable Long userId,
                                    HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            Long friendId = Long.valueOf(JwtUtil.parseToken(token).getSubject());
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
    public Result<PageResult<UserVo>> getFriendList(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "9") long size) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());

            IPage<UserVo> page = userService.getFriendList(userId, current, size);

            PageResult<UserVo> result = new PageResult<>(
                    page.getRecords(),
                    page.getTotal(),
                    page.getPages()
            );
            return Result.success("获取好友列表成功", result);
        }
        return Result.error("获取好友列表失败");
    }



    /**
     * 根据昵称查询用户
     */
    @GetMapping("getUserByNickName/{nickname}")
    public Result<PageResult<UserVo>> getUserByNickName(@PathVariable String nickname,
                                                        @RequestParam(defaultValue = "1") long current,
                                                        @RequestParam(defaultValue = "9") long size) {
        IPage<UserVo> page = userService.getUserByNickName(nickname, current, size);

        PageResult<UserVo> result = new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getPages()
        );
        return Result.success("查询用户成功", result);
    }



    /**
     * 根据用户id查询用户
     **/
    @GetMapping("/getUserById/{userId}")
    public Result<UserVo> getUserById(@PathVariable Long userId) {
        UserVo userVo = userService.getUserById(userId);
        return Result.success("查询用户成功", userVo);
    }


    /**
     * 搜索用户
     */
    @GetMapping("/search/{param}")
    public Result<PageResult<UserVo>> search(@PathVariable String param,
                                             HttpServletRequest request,
                                             @RequestParam(defaultValue = "1") long current,
                                             @RequestParam(defaultValue = "9") long size) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());
            IPage<UserVo> page = userService.search(param, userId, current, size);


            PageResult<UserVo> result = new PageResult<>(
                    page.getRecords(),
                    page.getTotal(),
                    page.getPages()
            );
            return Result.success("查询用户成功", result);
        }
        return Result.error("查询用户失败");
    }










}
