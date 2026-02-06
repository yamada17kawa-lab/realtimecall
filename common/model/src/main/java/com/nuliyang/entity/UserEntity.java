package com.nuliyang.entity;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@TableName("user") // 对应数据库表名
public class UserEntity {

    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class) // 将 Long 转为 String 给前端
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;


    //用户表没有的字段，仅仅辅助信令服务
    private String roomId;

    /**
     * 用户名（唯一，登录用）
     */
    @TableField(value = "user_name", condition = SqlCondition.EQUAL)
    private String userName;

    /**
     * 加密后的密码（BCrypt）
     */
    @TableField("password")
    private String password;

    /**
     * 昵称（可选）
     */
    @TableField("nick_name")
    private String nickName;

    /**
     * 手机号（可选，可用于找回密码）
     */
    @TableField("phone")
    private String phone;

    /**
     * 邮箱（可选）
     */
    @TableField("email")
    private String email;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 账号状态：0-离线，1-在线 2-正在通话
     */
    @TableField("status")
    private Integer status = 0;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    /**
     * 逻辑删除标识：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted = 0;


}
