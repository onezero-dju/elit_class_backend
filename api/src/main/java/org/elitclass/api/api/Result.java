package org.elitclass.api.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elitclass.api.error.ErrorCode;
import org.elitclass.api.error.ErrorCodeIfs;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Result {
    private Integer resultCode;
    private String resultMessage;
    private String resultDescription;

    public static Result OK(){
        return Result.builder()
                .resultCode(ErrorCode.OK.getErrorCode())
                .resultMessage(ErrorCode.OK.getErrorDescription())
                .resultDescription("성공")
                .build();
    }
    public static Result ERROR(ErrorCodeIfs errorCodeIfs){
        return Result.builder()
                .resultCode(errorCodeIfs.getErrorCode())
                .resultMessage(errorCodeIfs.getErrorDescription())
                .resultDescription("에러")
                .build();
    }
    public static Result ERROR(ErrorCodeIfs errorCodeIfs,Throwable tx){
        return Result.builder()
                .resultCode(errorCodeIfs.getErrorCode())
                .resultMessage(errorCodeIfs.getErrorDescription())
                .resultDescription(tx.getLocalizedMessage())
                .build();
    }
    public static Result ERROR(ErrorCodeIfs errorCodeIfs,String Description){
        return Result.builder()
                .resultCode(errorCodeIfs.getErrorCode())
                .resultMessage(errorCodeIfs.getErrorDescription())
                .resultDescription(Description)
                .build();
    }
}
