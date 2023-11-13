package com.example.foodrecommend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//使用
//return ApiResponse.success(data);
//return ApiResponse.fail(500, "Internal Server Error");
//或者
//import static com.example.foodrecommend.utils.R.success;
//success(this.userService.page(page, new QueryWrapper<>(user)));
@Data
@AllArgsConstructor
@NoArgsConstructor
public class R<T> {
    private int code;
    private String message;
    private T data;

    // 静态方法，用于创建成功的 R 对象
    public static <T> R<T> success(T data) {
        return new R<>(200, "Success", data);
    }

    // 静态方法，用于创建失败的 R 对象
    public static <T> R<T> failure(int code, String message) {
        return new R<>(code, message, null);
    }
}
