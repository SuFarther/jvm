package com.onecbuying.jvm.t2;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo3
 * @company 公司
 * @Description 演示软引用
 * -Xmx20m -XX:+PrintGCDetails -verbose:gc
 * @createTime 2022年08月21日 17:17:17
 */
public class Demo3 {
    private static final int _4MB = 4 * 1024 * 1024;



    public static void main(String[] args) throws IOException {
        /*List<byte[]> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new byte[_4MB]);
        }

        System.in.read();*/
        soft();


    }

    public static void soft() {
        // list --> SoftReference --> byte[]

        List<SoftReference<byte[]>> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SoftReference<byte[]> ref = new SoftReference<>(new byte[_4MB]);
            System.out.println(ref.get());
            list.add(ref);
            System.out.println(list.size());

        }
        System.out.println("循环结束：" + list.size());
        for (SoftReference<byte[]> ref : list) {
            System.out.println(ref.get());
        }
    }
}
