package com.nuliyang.mapper;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuliyang.vo.UserVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FriendMapper {


    /**
     * 获取申请添加好友列表
     * @param page
     * @param userId
     * @return
     */
    IPage<UserVo> getAddFriendByUserId(Page<UserVo> page, @Param("userId") Long userId);


    /**
     * 插入申请好友表
     * @param userId 用户id
     * @param friendId 好友id
     */
    @Insert("INSERT INTO add_friend (`user_id`, `friend_id`) VALUES (#{userId}, #{friendId})")
    void insertAddFriend(Long userId, Long friendId);


    /**
     * 根据userId删除申请好友表
     * @param userId
     */
    @Delete("DELETE FROM add_friend WHERE user_id = #{userId}")
    Integer deleteApplyByUserId(Long userId);



}
