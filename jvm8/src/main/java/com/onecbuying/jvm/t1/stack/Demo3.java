package com.onecbuying.jvm.t1.stack;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo3
 * @company 公司
 * @Description 演示线程死锁
 * 排查线程死锁问题
 *
 * 查询 nohup java com.onecbuying.jvm.t1.stack.Demo3 &
 * jstack 线程id
 * @createTime 2022年08月14日 17:54:54
 */
class A{};
class B{};

public class Demo3 {
    static A a = new A();
    static B b = new B();


    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            synchronized (a) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (b) {
                    System.out.println("我获得了 a 和 b");
                }
            }
        }).start();
        Thread.sleep(1000);
        new Thread(()->{
            synchronized (b) {
                synchronized (a) {
                    System.out.println("我获得了 a 和 b");
                }
            }
        }).start();
    }
}
