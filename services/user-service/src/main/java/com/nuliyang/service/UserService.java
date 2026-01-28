package com.nuliyang.service;

import com.nuliyang.dto.UserUpdateDto;
import com.nuliyang.entity.FriendEntity;
import com.nuliyang.entity.UserEntity;
import com.nuliyang.vo.UserVo;

import java.util.List;

public interface UserService {

    /**
     * 添加用户
     * @param userEntity
     */
    void adduser(UserEntity userEntity);


    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    UserEntity getUserByUsername(String username);


    /**
     * 修改用户信息
     * @param userUpdateDto
     */
    UserVo updateUser(UserUpdateDto userUpdateDto);

    /**
     * 删除用户
     * @param userId
     */
    void deleteUser(Long userId);


    /**
     * 添加好友
     * @param friendEntity
     */
    void addFriend(FriendEntity friendEntity);

    /**
     * 删除好友
     * @param userId friendId
     */
    void deleteFriend(Long userId, Long friendId);

    /**
     * 获取好友列表
     * @param userId
     * @return
     */
    List<UserVo> getFriendList(Long userId);
}
