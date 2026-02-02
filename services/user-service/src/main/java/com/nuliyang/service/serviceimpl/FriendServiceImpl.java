package com.nuliyang.service.serviceimpl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuliyang.mapper.FriendMapper;
import com.nuliyang.service.FriendService;
import com.nuliyang.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendMapper friendMapper;



    /**
     * 获取好友申请列表
     */
    @Override
    public IPage<UserVo> getApply(Long userId, long current, long size) {
        Page<UserVo> page = new Page<>(current, size);
        return friendMapper.getAddFriendByUserId(page, userId);
    }



    /**
     * 申请添加好友
     * @param userId
     * @param friendId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void applyFriend(Long userId, Long friendId) {
        friendMapper.insertAddFriend(userId, friendId);
    }


    /**
     * 删除好友申请
     * @param userId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteApply(Long userId) {
        friendMapper.deleteApplyByUserId(userId);
    }


}
