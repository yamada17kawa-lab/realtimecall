package com.nuliyang.service.serviceimpl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuliyang.RedisUtil;
import com.nuliyang.entity.UserEntity;
import com.nuliyang.mapper.FriendMapper;
import com.nuliyang.service.FriendService;
import com.nuliyang.vo.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendMapper friendMapper;

    private final ObjectMapper objectMapper;

    private final RedisUtil redisUtil;



    /**
     * 获取好友申请列表
     */
    @Override
    public IPage<UserVo> getApply(Long userId, long current, long size) throws JsonProcessingException {
        Page<UserVo> page = new Page<>(current, size);
        IPage<UserVo> userVoIPage = friendMapper.getAddFriendByUserId(page, userId);

        // 遍历好友列表，获取并更新用户信息
        for (UserVo userVo : userVoIPage.getRecords()) {
            Long id = userVo.getId();
            //从redis获取用户
            String userJson = redisUtil.get("ws:user:" + id);
            //有就改状态
            if ( userJson != null) {
                log.info("redis中有缓存，取缓存的数据");
                UserEntity userEntity = objectMapper.readValue(userJson, UserEntity.class);
                userVo.setStatus(userEntity.getStatus()).setRoomId(userEntity.getRoomId());
            }
        }

        return userVoIPage;
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
