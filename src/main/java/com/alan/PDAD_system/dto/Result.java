package com.alan.PDAD_system.dto;
import lombok.Data;

//该类的用途是统一响应结果
//Result定义为泛型类，通过泛型定义，使其可以适配不同类型的数据，
// 即data可以是不同的类型，这个在编译时，通过具体的编程类型确定。
// Response类包含的返回的数据data（由于采用泛型定义，类型可以是
// 任意类型），还包含成功与否的状态信息以及失败是的错误信息。
@Data
public class Result<T> {
    private Integer code;//业务状态码  0-成功  1-失败
    private String message;//提示信息
    private T data;//响应数据

    //无参数的构造函数，即创建对象时无任何参数。比如登录时，判断用于
    // 名和密码是否正确，此时只需要返回成功与否，而不需要任何其他额
    // 外数据，
    // 该构造函数使用Alt + Insert方式自动生成
    public Result() {
    }
    //带参数的构造函数，即使用传递的参数来创建对象。
    // 该构造函数使用Alt + Insert方式自动生成
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(1);
        result.setMessage(message);
        result.setData(null);
        return result;
    }
    // 成功时返回的数据方法，支持返回类型为 T（泛型）
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(0, message, data);  // 状态码0表示成功
    }
    //快速返回操作成功响应结果(带响应数据data)
    // 被static修饰的方法称为静态方法或类方法，这类方法可以直接
    // 通过类名调用，而不需要创建对象实例。静态方法中不能直接访问
    // 非静态成员变量或方法，但可以访问静态成员。
    //快速返回操作成功响应结果，但是不包含任何结果数据
    public static Result<String> success() {
        return new Result<>(0, "操作成功", null);
    }
    //快速返回操作出错响应结果，不包含任何结果数据
}