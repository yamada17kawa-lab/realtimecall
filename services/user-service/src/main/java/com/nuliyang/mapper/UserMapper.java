package com.nuliyang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nuliyang.dto.UserUpdateDto;
import com.nuliyang.entity.FriendEntity;
import com.nuliyang.entity.UserEntity;
import com.nuliyang.vo.UserVo;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM `user` WHERE username = #{username}")
    UserEntity findByUsername(String username);


    /**
     * 添加用户
     *
     * @param username 用户名
     * @param password 密码
     */
    @Insert("INSERT INTO `user` (username, password) VALUES (#{username}, #{password})")
    void addUser(String username, String password);


    /**
     * 修改用户信息
     *
     * @param userUpdateDto 用户信息
     */
    void updateUser(UserUpdateDto userUpdateDto);


    /**
     * 添加好友
     *
     * @param friendEntity 好友信息
     */
    void addFriend(FriendEntity friendEntity);


    /**
     * 根据用户id查询用户
     *
     * @param friendId 用户id
     * @return 用户信息
     */
    @Select("SELECT * FROM `user` WHERE id = #{friendId}")
    UserEntity findUserById(Long friendId);

    /**
     * 根据用户id查询好友id
     *
     * @param userId 用户id
     * @return 好友id
     */
    @Select("SELECT friend_id FROM `user_friend` WHERE user_id = #{userId}")
    List<Long> findFriendIdsByUserId(Long userId);

    /**
     * 删除用户
     *
     * @param userId 用户id
     */
    @Delete("DELETE FROM `user` WHERE id = #{userId}")
    void deleteUserById(Long userId);


    /**
     * 根据userId删除好友
     *
     * @param userId 用户id
     */
    @Delete("DELETE FROM `user_friend` WHERE user_id = #{userId}")
    void deleteFriendsByUserId(Long userId);


    /**
     * 删除好友
     * @param userId 用户id
     * @param friendId 好友id
     */
    @Delete("DELETE FROM `user_friend` WHERE `user_id` = #{userId} AND `friend_id` = #{friendId}")
    void deleteFriendByUserIdAndFriendId(@Param("userId") Long userId, @Param("friendId") Long friendId);


    /**
     * 根据userId获取好友列表
     * @param userId 用户id
     * @return 好友列表
     */
    List<UserVo> getFriendListByUserId(Long userId);
}
