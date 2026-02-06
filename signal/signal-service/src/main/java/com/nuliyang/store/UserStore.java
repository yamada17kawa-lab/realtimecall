package com.nuliyang.store;

import com.nuliyang.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserStore {

    @Select("select * from user where id = #{userId}")
    UserEntity getUserByUserId(Long userId);

}
