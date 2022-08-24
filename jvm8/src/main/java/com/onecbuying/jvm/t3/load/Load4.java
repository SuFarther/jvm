package com.onecbuying.jvm.t3.load;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Load4
 * @company 公司
 * @Description p148 类加载-练习
 * @createTime 2022年08月24日 13:50:50
 */
public class Load4 {
    public static void main(String[] args) {
        System.out.println(E.a);
        System.out.println(E.b);
        System.out.println(E.c);

    }
}

class E {
    public static final int a = 10;
    public static final String b = "hello";
    public static final Integer c = 20;  // Integer.valueOf(20)
    static {
        System.out.println("init E");
    }
}
