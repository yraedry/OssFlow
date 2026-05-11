package com.ossflow.identity.auth.domain;

public enum AccountEventType {
    REGISTER,
    LOGIN,
    LOGIN_FAILED,
    LOGOUT,
    PASSWORD_RESET,
    TOKEN_REUSE_DETECTED
}
