package com.alan.PDAD_system;
import org.junit.jupiter.api.Test;

public class ThreadLocalTest {
    @Test
    public void testThreadLocalSetAndGet(){
        //定义一个ThreadLocal对象
        ThreadLocal tl = new ThreadLocal();

        //创建两个线程
        new Thread(()->{
            tl.set("ZZZZ");

            System.out.println(Thread.currentThread().getName()+": "+tl.get());
            System.out.println(Thread.currentThread().getName()+": "+tl.get());
            System.out.println(Thread.currentThread().getName()+": "+tl.get());
        }, "BBB").start();

        new Thread(()->{
            tl.set("LLLL");

            System.out.println(Thread.currentThread().getName()+": "+tl.get());
            System.out.println(Thread.currentThread().getName()+": "+tl.get());
            System.out.println(Thread.currentThread().getName()+": "+tl.get());
        }, "HHHH").start();
    }
}
