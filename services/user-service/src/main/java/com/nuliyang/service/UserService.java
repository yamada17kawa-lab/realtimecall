package com.nuliyang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.nuliyang.dto.UserUpdateDto;
import com.nuliyang.entity.FriendEntity;
import com.nuliyang.entity.UserEntity;
import com.nuliyang.vo.UserVo;


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
    IPage<UserVo> getFriendList(Long userId, long current, long size);


    /**
     * 根据id查询用户
     * @param userId
     * @return
     */
    UserVo getUserById(Long userId);


    /**
     * 根据昵称查询用户
     * @param nickname
     * @return
     */
    IPage<UserVo> getUserByNickName(String nickname, long current, long size);

    /**
     * 搜索用户
     * @param param
     * @param userId
     * @return
     */
    IPage<UserVo> search(String param, Long userId, long current, long size);


}
