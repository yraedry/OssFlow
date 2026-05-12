package com.ossflow.identity.auth.domain;

public enum AccountEventType {
    REGISTER,
    LOGIN,
    LOGIN_FAILED,
    LOGOUT,
    PASSWORD_RESET,
    PASSWORD_CHANGED,
    TOKEN_REUSE_DETECTED
}
