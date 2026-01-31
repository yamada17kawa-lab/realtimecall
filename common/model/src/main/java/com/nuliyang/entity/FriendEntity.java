package com.nuliyang.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("user_friend")
@Accessors(chain = true)
public class FriendEntity {

    @JsonSerialize(using = ToStringSerializer.class) // 将 Long 转为 String 给前端
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("friend_id")
    private Long friendId;

    //0离线 1在线
    @TableField("status")
    private Integer status;

    @TableField("created_at")
    private Long createdAt;
}
