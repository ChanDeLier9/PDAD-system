package com.alan.PDAD_system.utils;

/**
 * ThreadLocal 工具类
 */
@SuppressWarnings("all")
public class ThreadLocalUtil {
    //提供ThreadLocal对象, private static final这三个
    // 关键字一起使用时，表示该成员是类级别的常量，并且只能在
    // 类内部访问。final表示一旦被赋值后就不能再修改。
    // 因此THREAD_LOCAL这里其实起到了一个类似线程内部局部常量
    // （可被初始赋值一次）的作用（只在该线程内起作用）。
    // THREAD_LOCAL只能通过类提供的get和set方法访问，
    private static final ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    //根据键获取值
    @SuppressWarnings("unchecked")
    public static <T> T get(){
        return (T) THREAD_LOCAL.get();
    }

    //存储键值对
    public static void set(Object value){
        THREAD_LOCAL.set(value);
    }

    //清除ThreadLocal 防止内存泄漏
    public static void remove(){
        THREAD_LOCAL.remove();
    }
}