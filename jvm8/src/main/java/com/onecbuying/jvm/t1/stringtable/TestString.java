package com.onecbuying.jvm.t1.stringtable;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName TestString
 * @company 公司
 * @Description 演示字符串字面量也是【延迟】成为对象的（StringTable字符串延迟加载）
 * @createTime 2022年08月17日 16:11:11
 */
public class TestString {
    public static void main(String[] args) {
        int x = args.length;
        System.out.println(); // 字符串个数 2275

        System.out.print("1");
        System.out.print("2");
        System.out.print("3");
        System.out.print("4");
        System.out.print("5");
        System.out.print("6");
        System.out.print("7");
        System.out.print("8");
        System.out.print("9");
        System.out.print("0");
        System.out.print("1"); // 字符串个数 2285
        System.out.print("2");
        System.out.print("3");
        System.out.print("4");
        System.out.print("5");
        System.out.print("6");
        System.out.print("7");
        System.out.print("8");
        System.out.print("9");
        System.out.print("0");
        System.out.print(x); // 字符串个数
    }
}
