package org.elitclass.api.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode implements ErrorCodeIfs{

    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), 400, "잘못된 요청"),
    NULL_POINT(HttpStatus.INTERNAL_SERVER_ERROR.value(), 512, "NULL POINT"),
    OK(200, 200, "성공"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), 500, "서버 에러"),
    ;

    private final Integer httpStatusCode;
    private final Integer errorCode;
    private final String errorDescription;

}
