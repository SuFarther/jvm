package com.onecbuying.jvm.t1.direct;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo26
 * @company 公司
 * @Description 禁用显式回收对直接内存的影响
 * @createTime 2022年08月18日 19:08:08
 */
public class Demo26 {
    static int _1Gb = 1024 * 1024 * 1024;

    /*
     * -XX:+DisableExplicitGC 显式的
     */
    public static void main(String[] args) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(_1Gb);
        System.out.println("分配完毕...");
        System.in.read();
        System.out.println("开始释放...");
        byteBuffer = null;
        System.gc(); // 显式的垃圾回收，Full GC
        System.in.read();
    }
}
