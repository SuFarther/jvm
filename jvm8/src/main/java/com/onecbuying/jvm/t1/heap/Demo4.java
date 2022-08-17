package com.onecbuying.jvm.t1.heap;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo4
 * @company 公司
 * @Description 演示堆内存诊断
 *   jps工具：查看当前系统中有哪些java进程
 *   jmap工具：查看堆内存占用情况 jmap -heap pid
 *   jstack 工具：线程监控
 *   jconsole工具：图形界面的，多功能的检测工具，可以连续监测2
 *  jvisualvm工具：图形界面的，多功能的检测工具，可以连续监测；还有dump
 *  @createTime 2022年08月14日 20:14:14
 */
public class Demo4 {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("1...");
        Thread.sleep(30000);
        // 10 Mb
        byte[] array = new byte[1024 * 1024 * 10];
        System.out.println("2...");
        Thread.sleep(20000);
        array = null;
        System.gc();
        // 垃圾回收
        System.out.println("3...");
        Thread.sleep(1000000L);
    }
}
