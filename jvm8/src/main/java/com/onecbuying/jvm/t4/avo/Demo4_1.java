package com.onecbuying.jvm.t4.avo;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo4_1
 * @company 公司
 * @Description p168 JMM-原子性-问题
 * @createTime 2022年08月24日 15:35:35
 */
public class Demo4_1 {
    static int i = 0;

    static Object obj = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (obj) {
                for (int j = 0; j < 50000; j++) {
                    i++;
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (obj) {
                for (int j = 0; j < 50000; j++) {
                    i--;
                }
            }
        });
        t1.start();
        t2.start();

        t1.join();
        t2.join();
        System.out.println(i);
    }
}
