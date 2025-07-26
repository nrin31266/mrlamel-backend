package com.rin.mrlamel.common.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiRes<T> {
    @Builder.Default
    int code = 200;
    T data;
    String message;

    public static <T> ApiRes<T> success(T data) {
        return ApiRes.<T>builder()
                .code(200)
                .data(data)
                .message("Success")
                .build();
    }
}
