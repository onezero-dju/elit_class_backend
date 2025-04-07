package org.elitclass.api.api;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.elitclass.api.error.ErrorCodeIfs;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Api<T> {
    private Result result;
    @Valid
    private T body;

    public static <T> Api<T> OK(T Data) {
        var api = new Api<T>();
        api.result = Result.OK();
        api.body = Data;
        return api;
    }
    public static Api<Object> ERROR(Result Result) {
        var api = new Api<Object>();
        api.result = Result;
        return api;
    }
    public static Api<Object> ERROR(ErrorCodeIfs errorCodeIfs){
        var api = new Api<Object>();
        api.result = Result.ERROR(errorCodeIfs);
        return api;
    }
    public static Api<Object> ERROR(ErrorCodeIfs errorCodeIfs,Throwable tx){
        var api = new Api<Object>();
        api.result = Result.ERROR(errorCodeIfs,tx);
        return api;
    }
    public static Api<Object> ERROR(ErrorCodeIfs errorCodeIfs,String description){
        var api = new Api<Object>();
        api.result = Result.ERROR(errorCodeIfs,description);
        return api;
    }


}
