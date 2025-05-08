package org.elitclass.api.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode implements ErrorCodeIfs {
    // 공통(Common)
    OK(200, "success", HttpStatus.OK),
    SERVER_ERROR(500, "internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    // User (1xxx)
    USER_NOT_FOUND(1404, "user id not found", HttpStatus.NOT_FOUND),
    USER_PROFILE_SUCCESS(1200, "Successfully viewed user profile", HttpStatus.OK),

    // Class (2xxx)
    CLASS_NOT_FOUND(2404, "Class ID not found", HttpStatus.NOT_FOUND),
    NETWORK_ERROR(2404, "network error", HttpStatus.NOT_FOUND),
    CLASS_INQUIRY_FALSE(2401, "class inquiry false", HttpStatus.BAD_REQUEST),
    CLASS_SUCCESS(2200, "Class inquiry successfully", HttpStatus.OK),

    // Admin (10xxx)
    ADMIN_CLASS_NOT_FOUND(10404, "Class ID not found", HttpStatus.NOT_FOUND),
    ADMIN_USER_NOT_FOUND(10404, "user id not found", HttpStatus.NOT_FOUND),
    ADMIN_SUCCESS(10200, "Successfully processed", HttpStatus.OK);
    private final Integer code;
    private final String message;
    private final HttpStatus status;

    @Override
    public Integer getErrorCode() {
        return code;
    }

    @Override
    public String getErrorDescription() {
        return message;
    }

    @Override
    public Integer getHttpStatusCode() {
        return status.value();
    }
}
