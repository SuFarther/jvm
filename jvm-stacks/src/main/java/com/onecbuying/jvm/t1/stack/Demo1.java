package com.onecbuying.jvm.t1.stack;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo1
 * @company 公司
 * @Description 演示栈帧
 * debug调试
 * @createTime 2022年08月14日 05:22:22
 */
public class Demo1 {
    public static void main(String[] args) {
       method1();
    }

    private static void method1() {
        method2(1,2);
    }

    private static int method2(int a, int b) {
        int c = a + b;
        return c;
    }
}
