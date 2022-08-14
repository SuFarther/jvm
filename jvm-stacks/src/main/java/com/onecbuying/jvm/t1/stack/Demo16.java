package com.onecbuying.jvm.t1.stack;

/**
 * @author 演示cpu
 * @version 1.0
 * @ClassName Demo16
 * @company 公司
 * @Description 演示 cpu 占用过高
 * 1、用top定位哪个进程对cpu的占用过高
 * 2、ps H -eo pid,tid,%cpu | grep 进程id (用ps命令进一步定位是哪个线程引起的cpu占用过高)
 * 3、jstack 进程id
 *
 * 进程数十进制演算为16进制的nid
 * @createTime 2022年08月14日 17:41:41
 */
public class Demo16 {
    public static void main(String[] args) {
        new Thread(null, () -> {
            System.out.println("1...");
            while(true) {

            }
        }, "thread1").start();


        new Thread(null, () -> {
            System.out.println("2...");
            try {
                Thread.sleep(1000000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "thread2").start();

        new Thread(null, () -> {
            System.out.println("3...");
            try {
                Thread.sleep(1000000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "thread3").start();
    }
}
