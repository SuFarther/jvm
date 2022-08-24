package com.onecbuying.jvm.t4.avo;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo4_2
 * @company 公司
 * @Description P170(JMM-可见性-解决)
 * volatile可见性它保证的是多个线程之间，一个线程对volatile变量的修改对另一个线程可见,不能保证原子性，仅用在一个写线程,多个读线程的情况
 * @createTime 2022年08月24日 15:48:48
 */
public class Demo4_2 {
    static boolean run = true;

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(()->{
            while(run){
                // ....
                System.out.println(1);
            }
        });
        t.start();

        Thread.sleep(1000);
        run = false; // 线程t不会如预想的停下来
    }
}
