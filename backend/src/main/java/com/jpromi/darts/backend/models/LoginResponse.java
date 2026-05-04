package com.jpromi.darts.backend.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.jpromi.darts.backend.enums.ErrorCode;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String token;

    private Boolean totpRequired;

    @JsonSerialize(using = ToStringSerializer.class)
    @Builder.Default
    private ErrorCode error = ErrorCode.NONE;
}
