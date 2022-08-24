package com.onecbuying.jvm.t4.avo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo4_4
 * @company 公司
 * @Description P177(CAS-原子类)
 * @createTime 2022年08月24日 16:19:19
 */
public class Demo4_4 {
    // 创建原子整数对象
    private static AtomicInteger i = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                i.getAndIncrement();  // 获取并且自增  i++
//                i.incrementAndGet();  // 自增并且获取  ++i
            }
        });

        Thread t2 = new Thread(() -> {
            for (int j = 0; j < 5000; j++) {
                i.getAndDecrement(); // 获取并且自减  i--
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(i);
    }
}
