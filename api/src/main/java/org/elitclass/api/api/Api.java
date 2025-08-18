package org.elitclass.api.api;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elitclass.api.domain.classes.service.ClassService;
import org.elitclass.api.error.ErrorCodeIfs;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Api<T> {

    private ClassService classService;
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

    @ApiResponse(responseCode = "200", description = "클래스 소유자 비교 성공")
    @GetMapping("/class/{id}/isowner")
    public Api<Boolean> isOwner(@PathVariable Long id, Authentication authentication) {
        return Api.OK(classService.isOwner(authentication, id)); // body: true/false
    }

}
