package com.onecbuying.jvm;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo21
 * @company 公司
 * @Description StringTable面试题
 * @createTime 2022年08月16日 22:11:11
 */
public class Demo21 {
    public static void main(String[] args) {
        String s1 = "a";
        String s2 = "b";
        String s3 = "a" + "b";
        String s4 = s1 + s2;
        String s5 = "ab";
        String s6 = s4.intern();

// 问
        System.out.println(s3 == s4);
        System.out.println(s3 == s5);
        System.out.println(s3 == s6);

        String x2 = new String("c") + new String("d"); // new String("cd")
        x2.intern(); // "cd"
        String x1 = "cd";

// 问，如果调换了【最后两行代码】的位置呢，如果是jdk1.6呢
        System.out.println(x1 == x2);
    }
}
