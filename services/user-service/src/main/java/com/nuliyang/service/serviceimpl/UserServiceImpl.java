package com.nuliyang.service.serviceimpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nuliyang.BizException;
import com.nuliyang.ErrorCode;
import com.nuliyang.RedisUtil;
import com.nuliyang.dto.UserUpdateDto;
import com.nuliyang.entity.FriendEntity;
import com.nuliyang.entity.UserEntity;
import com.nuliyang.mapper.FriendMapper;
import com.nuliyang.mapper.UserMapper;
import com.nuliyang.service.UserService;
import com.nuliyang.vo.UserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {


    private final UserMapper userMapper;

    private final ObjectMapper objectMapper;
    private final FriendMapper friendMapper;
    private final RedisUtil redisUtil;

    /**
     * 添加用户
     * @param userEntity
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserVo adduser(UserEntity userEntity) {
        //先根据用户名从数据库中查出是否已存在
        //如果存在则返回错误
        if (userMapper.findByUsername(userEntity.getUserName()) != null) {
            log.error("用户名已存在: {}", userEntity.getUserName());
            throw new BizException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        //不存在添加该用户
        userEntity.setStatus(0);
        userEntity.setPassword(BCrypt.hashpw(userEntity.getPassword(), BCrypt.gensalt()));
        userEntity.setCreateTime(System.currentTimeMillis());
        userEntity.setUpdateTime(System.currentTimeMillis());
        userMapper.insert(userEntity);
        UserEntity userEntity1 = userMapper.findByUsername(userEntity.getUserName());
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userEntity1, userVo);
        return userVo;
    }


    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    @Override
    public UserEntity getUserByUsername(String username) {
        UserEntity user = userMapper.findByUsername(username);
        log.info("根据用户名查询到用户: {}", user);
        if (user == null){
            //用户不存在
            log.error("用户不存在: {}", username);
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    /**
     * 修改用户信息
     * @param userUpdateDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserVo updateUser(UserUpdateDto userUpdateDto) {
        //判断该用户操作是否为修改密码
        //0 不修改密码 1 修改密码
        //不改密码
        if (userUpdateDto.getIsUpdatePassword() == 0) {
            log.info("不改密码");
            userUpdateDto.setUpdateTime(System.currentTimeMillis());

            userMapper.updateUser(userUpdateDto);
            UserEntity userEntity1 = userMapper.findByUsername(userUpdateDto.getUserName());
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(userEntity1, userVo);
            return userVo;

        }
        //修改密码
        else if (userUpdateDto.getIsUpdatePassword() == 1){
            log.info("改密码");
            //对比旧密码
            //密码不一样报错
            if (!BCrypt.checkpw(userUpdateDto.getOldPassword(), userMapper.findByUsername(userUpdateDto.getUserName()).getPassword())) {
                throw new BizException(ErrorCode.PASSWORD_ERROR);
            }else {
                userUpdateDto.setUpdateTime(System.currentTimeMillis());
                userUpdateDto.setNewPassword(BCrypt.hashpw(userUpdateDto.getNewPassword(), BCrypt.gensalt()));

                userMapper.updateUser(userUpdateDto);
                UserEntity userEntity = userMapper.findByUsername(userUpdateDto.getUserName());
                UserVo userVo = new UserVo();
                BeanUtils.copyProperties(userEntity, userVo);
                return userVo;
            }
        }
        throw new BizException(ErrorCode.MODIFY_PASSWORD_PARAMETER_ERROR);
    }


    /**
     * 删除用户
     * @param userId
     */
    @Override
    public void deleteUser(Long userId) {
        //删除自己
        userMapper.deleteUserById(userId);
        log.info("删除用户: {}", userId);
        //删除好友
        userMapper.deleteFriendsByUserId(userId);
        log.info("删除用户: {} 的所有好友", userId);
    }


    /**
     * 添加好友
     * @param friendEntity
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addFriend(FriendEntity friendEntity) {
        Long myId = friendEntity.getUserId();
        log.info("myId； {}", myId);

        //先查是否有这个好友
        if (userMapper.findUserById(friendEntity.getUserId()) == null) {
            throw new BizException(ErrorCode.FRIEND_NOT_FOUND);
        }
        //检查friend_id是否是自己
        if (friendEntity.getUserId().equals(friendEntity.getFriendId())) {
            throw new BizException(ErrorCode.FRIEND_CANNOT_BE_YOURSELF);
        }
        //检查是否已经是好友
        List<Long> friendIds = userMapper.findFriendIdsByUserId(friendEntity.getUserId());
        if (friendIds.contains(friendEntity.getFriendId())) {
            throw new BizException(ErrorCode.FRIEND_ALREADY_EXISTS);
        }
        //给好友设置创建时间
        friendEntity.setCreatedAt(System.currentTimeMillis());
        //给好友获取在线离线状态
        friendEntity.setStatus(userMapper.getUserById(friendEntity.getFriendId()).getStatus() );
        log.info("添加好友第一条数据: {}", friendEntity);
        //添加好友第一条数据
        userMapper.addFriend(friendEntity);


        //添加好友第二条数据
        Long friendId = friendEntity.getUserId();
        Long userId = friendEntity.getFriendId();
        friendEntity.setUserId(userId);
        friendEntity.setFriendId(friendId);
        //给好友获取在线离线状态
        friendEntity.setStatus(userMapper.getUserById(friendEntity.getFriendId()).getStatus() );
        log.info("添加好友第二条数据: {}", friendEntity);
        userMapper.addFriend(friendEntity);
        //添加好友成功后，然后删除申请表的相关数据
        Integer num = friendMapper.deleteApplyByUserId(myId);
        log.info("删除申请表数据{}条", num);
    }

    /**
     * 删除好友
     * @param userId friend_id
     */
    @Override
    public void deleteFriend(Long userId, Long friendId) {
        //检查是否是自己
        if (userId.equals(friendId)) {
            throw new BizException(ErrorCode.FRIEND_CANNOT_BE_YOURSELF);
        }
        //检查是否是好友
        List<Long> friendIds = userMapper.findFriendIdsByUserId(userId);
        if (!friendIds.contains(friendId)) {
            throw new BizException(ErrorCode.FRIEND_NOT_FOUND);
        }
        //删除好友数据一
        userMapper.deleteFriendByUserIdAndFriendId(userId, friendId);

        //删除好友数据二
        userMapper.deleteFriendByUserIdAndFriendId(friendId, userId);

    }


    /**
     * 根据id获取用户
     * @param userId
     * @return
     */
    @Override
    public UserVo getUserById(Long userId) {
        UserVo userVo = userMapper.getUserById(userId);
        if (userVo == null){
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        return userVo;
    }




    /**
     * 获取好友列表
     */
    @Override
    public IPage<UserVo> getFriendList(Long userId, long current, long size) throws JsonProcessingException {
        Page<UserVo> page = new Page<>(current, size);
        IPage<UserVo> friendList = userMapper.getFriendListByUserId(page, userId);

        // 遍历好友列表，获取并更新用户信息
        for (UserVo userVo : friendList.getRecords()) {
            Long id = userVo.getId();
            //从redis获取用户
            String userJson = redisUtil.get("ws:user:" + id);
            //有就改状态
            if ( userJson != null) {
                UserEntity userEntity = objectMapper.readValue(userJson, UserEntity.class);
                userVo.setStatus(userEntity.getStatus()).setRoomId(userEntity.getRoomId());
            }
        }
        return friendList;
    }



    /**
     * 根据昵称获取用户
     * @param nickname
     * @return
     */
    @Override
    public IPage<UserVo> getUserByNickName(String nickname, long current, long size) {
        IPage<UserVo> page = new Page<>(current, size);
        return userMapper.getUserByNickName(page, nickname);
    }


    /**
     * 搜索用户
     * @param param
     * @param userId
     * @param current
     * @param size
     * @return
     */
    @Override
    public IPage<UserVo> search(String param, Long userId, long current, long size) throws JsonProcessingException {
        //查出当前用户
        UserVo userVo = userMapper.getUserById(userId);
        if (userVo == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        //检查是不是搜索自己
        if (param.equals(userVo.getNickName()) || param.equals(userVo.getId().toString())) {
            throw new BizException(ErrorCode.SEARCH_SELF);
        }
        //开始查询
        //已经添加的好友就不参与搜索，过滤掉
        Page<UserVo> page = new Page<>(current, size);
        IPage<UserVo> userVoIPage = userMapper.search(page, param, userId);


        // 遍历好友列表，获取并更新用户信息
        for (UserVo userVo2 : userVoIPage.getRecords()) {
            Long id = userVo2.getId();
            //从redis获取用户
            String userJson = redisUtil.get("ws:user:" + id);
            //有就改状态
            if ( userJson != null) {
                UserEntity userEntity = objectMapper.readValue(userJson, UserEntity.class);
                userVo2.setStatus(userEntity.getStatus()).setRoomId(userEntity.getRoomId());
            }
        }
        return userVoIPage;
    }




}
