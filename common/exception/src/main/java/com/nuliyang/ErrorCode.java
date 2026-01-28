package com.nuliyang;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // 通用
    SYSTEM_ERROR(5000, "系统异常"),
    PARAM_ERROR(400, "参数错误"),

    // 业务相关（示例）
    FRIEND_ALREADY_EXISTS(10001, "好友已存在"),
    FRIEND_NOT_FOUND(10002, "好友不存在"),
    NO_PERMISSION(10003, "无操作权限"),
    FRIEND_CANNOT_BE_YOURSELF(10004, "朋友不能是你自己"),
    PASSWORD_ERROR(10005, "密码错误"),
    MODIFY_PASSWORD_PARAMETER_ERROR(10006, "修改密码参数错误"),
    USERNAME_ALREADY_EXISTS(10007, "用户名已存在"),
    USER_NOT_FOUND(500, "用户不存在"),
    TOKEN_INVALID(401, "Token无效"),
    UNKNOWN_SIGNAL_TYPE(400, "未知的信令类型");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
