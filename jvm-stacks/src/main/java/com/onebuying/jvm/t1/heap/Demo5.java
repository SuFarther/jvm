package com.onebuying.jvm.t1.heap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo5
 * @company 公司
 * @Description 演示堆内存溢出 java.lang.OutOfMemoryError: Java heap space
 * -Xmx8m
 * @createTime 2022年08月14日 20:01:01
 */
public class Demo5 {
    public static void main(String[] args) {
        int i = 0;
        try {
            List<String> list = new ArrayList<>();
            String a = "hello";
            while (true) {
                list.add(a); // hello, hellohello, hellohellohellohello ...
                a = a + a;  // hellohellohellohello
                i++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println(i);
        }
    }
}
