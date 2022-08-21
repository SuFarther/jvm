package com.onecbuying.jvm.t2;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo5
 * @company 公司
 * @Description 演示弱引用
 * -Xmx20m -XX:+PrintGCDetails -verbose:gc
 * @createTime 2022年08月21日 17:31:31
 */
public class Demo5 {
    private static final int _4MB = 4 * 1024 * 1024;

    public static void main(String[] args) {
        //  list --> WeakReference --> byte[]
        List<WeakReference<byte[]>> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            WeakReference<byte[]> ref = new WeakReference<>(new byte[_4MB]);
            list.add(ref);
            for (WeakReference<byte[]> w : list) {
                System.out.print(w.get()+" ");
            }
            System.out.println();

        }
        System.out.println("循环结束：" + list.size());
    }
}
