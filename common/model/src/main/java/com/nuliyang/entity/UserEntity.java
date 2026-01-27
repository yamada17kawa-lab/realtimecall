package com.nuliyang.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
@TableName("user") // 对应数据库表名
public class UserEntity {

    /**
     * 用户ID（主键，自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（唯一，登录用）
     */
    @TableField(value = "username", condition = SqlCondition.EQUAL)
    private String username;

    /**
     * 加密后的密码（BCrypt）
     */
    @TableField("password")
    private String password;

    /**
     * 昵称（可选）
     */
    @TableField("nickname")
    private String nickname;

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
     * 账号状态：0-正常，1-禁用
     */
    @TableField("status")
    private Integer status = 0;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted = 0;
}
