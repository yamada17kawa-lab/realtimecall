package com.nuliyang.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nuliyang.vo.UserVo;

public interface FriendService {


    /**
     * 获取好友申请列表
     * @param userId
     * @param current
     * @param size
     * @return
     */
    IPage<UserVo> getApply(Long userId, long current, long size) throws JsonProcessingException;


    /**
     * 申请好友
     * @param userId
     * @param friendId
     */
    void applyFriend(Long userId, Long friendId);


    /**
     * 删除好友申请
     * @param userId
     */
    void deleteApply(Long userId);
}
