package com.onecbuying.jvm.t1.stack;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo17
 * @company 公司
 * @Description 局部变量的线程安全问题
 * m1下的StringBuilder方法是私有的，是线程安全的,m2下作为参数传进去StringBuilder sb代表着多个线程可以同时操作StringBuilder sb对象
 * 主方法也在操作m2里面的StringBuilder sb，这种情况下StringBuilder sb的对象就是不安全的,多个线程共享了同一个对象
 * m3里面的局部变量StringBuilder sb的变量sb作为参数返回了，返回了就意味着其他线程有可能拿到这个对象,并发的去修改他,也会造成线程安全问题。
 * @createTime 2022年08月14日 12:25:25
 */
public class Demo17 {
    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(4);
        sb.append(5);
        sb.append(6);
        new Thread(()->{
            m2(sb);
        }).start();
    }

    public static void m1() {
        StringBuilder sb = new StringBuilder();
        sb.append(1);
        sb.append(2);
        sb.append(3);
        System.out.println(sb.toString());
    }

    public static void m2(StringBuilder sb) {
        sb.append(1);
        sb.append(2);
        sb.append(3);
        System.out.println(sb.toString());
    }

    public static StringBuilder m3() {
        StringBuilder sb = new StringBuilder();
        sb.append(1);
        sb.append(2);
        sb.append(3);
        return sb;
    }
}
