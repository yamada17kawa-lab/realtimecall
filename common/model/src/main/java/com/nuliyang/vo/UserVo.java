package com.nuliyang.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserVo {
    private Long id;
    private String userName;
    private String nickName;
    private String email;
    private String phone;
    private String avatar;
    //0 离线 1 在线
    private Integer status = 0;
}
