package com.jpromi.darts.backend.enums;

public enum ErrorCode {
    NONE("0", ""),
    INVALID_LOGIN("001-01", "Invalid Username or Password"),
    INVALID_TOTP("001-02", "Invalid TOTP"),
    TOTP_DISABLED("001-03", "TOTP is disabled"),
    INVALID_SESSION("002-01", "Invalid Session");

    private String code;
    private String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() { return code; }
    public String message() { return message; }
    public String toString() { if(!code.equals("0")) return code + ": " + message; else return ""; }
}
