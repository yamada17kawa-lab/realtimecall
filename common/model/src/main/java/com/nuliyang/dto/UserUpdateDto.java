package com.nuliyang.dto;

import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
public class UserUpdateDto {

    private Long userId;
    private String userName;
    private String nickName;
    private String oldPassword;
    private String newPassword;
    private String email;
    private String phone;
    private String avatar;
    //0 不更新密码 1 更新密码
    private Integer isUpdatePassword;
    //逻辑删除标识：0-未删除，1-已删除
    private Integer isDeleted = 0;
    private Long updateTime;

}
