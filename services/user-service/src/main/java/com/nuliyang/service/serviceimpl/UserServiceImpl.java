package com.nuliyang.service.serviceimpl;

import com.nuliyang.BizException;
import com.nuliyang.ErrorCode;
import com.nuliyang.dto.UserUpdateDto;
import com.nuliyang.entity.FriendEntity;
import com.nuliyang.entity.UserEntity;
import com.nuliyang.mapper.UserMapper;
import com.nuliyang.service.UserService;
import com.nuliyang.vo.UserVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserMapper userMapper;

    /**
     * 添加用户
     * @param userEntity
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void adduser(UserEntity userEntity) {
        //先根据用户名从数据库中查出是否已存在
        //如果存在则返回错误
        if (userMapper.findByUsername(userEntity.getUsername()) != null) {
            throw new BizException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        //不存在添加该用户
        userEntity.setPassword(BCrypt.hashpw(userEntity.getPassword(), BCrypt.gensalt()));
        userMapper.addUser(userEntity.getUsername(), userEntity.getPassword());
    }


    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    @Override
    public UserEntity getUserByUsername(String username) {
        UserEntity user = userMapper.findByUsername(username);
        if (user == null){
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
            UserEntity userEntity = userMapper.findByUsername(userUpdateDto.getUsername());
            if (userEntity != null && !userEntity.getId().equals(userUpdateDto.getUserId())) {
                throw new BizException(ErrorCode.USERNAME_ALREADY_EXISTS);
            }
            userMapper.updateUser(userUpdateDto);
            UserEntity userEntity1 = userMapper.findByUsername(userUpdateDto.getUsername());
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(userEntity1, userVo);
            return userVo;

        }
        //修改密码
        else if (userUpdateDto.getIsUpdatePassword() == 1){
            //对比旧密码
            //密码不一样报错
            if (!BCrypt.checkpw(userUpdateDto.getOldPassword(), userMapper.findByUsername(userUpdateDto.getUsername()).getPassword())) {
                throw new BizException(ErrorCode.PASSWORD_ERROR);
            }else {
                userUpdateDto.setNewPassword(BCrypt.hashpw(userUpdateDto.getNewPassword(), BCrypt.gensalt()));
                userMapper.updateUser(userUpdateDto);
                UserEntity userEntity = userMapper.findByUsername(userUpdateDto.getUsername());
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
        //删除好友
        userMapper.deleteFriendsByUserId(userId);
    }


    /**
     * 添加好友
     * @param friendEntity
     */
    @Override
    public void addFriend(FriendEntity friendEntity) {
        //先查是否有这个好友
        if (userMapper.findUserById(friendEntity.getFriendId()) == null) {
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
        //给好友设置默认值
        friendEntity.setCreatedAt(LocalDateTime.parse(LocalDateTime.now().toString()));
        friendEntity.setStatus("0");
        userMapper.addFriend(friendEntity);
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
        //删除好友
        userMapper.deleteFriendByUserIdAndFriendId(userId, friendId);

    }


    /**
     * 获取好友列表
     */
    @Override
    public List<UserVo> getFriendList(Long userId) {
        //获取好友列表
        List<UserVo> friendList = userMapper.getFriendListByUserId(userId);
        //列表为空则返回空列表
        if (friendList == null) return Collections.emptyList();
        return friendList;
    }
}
