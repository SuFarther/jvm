package com.onecbuying.jvm;

/**
 * @author 速冻
 * @version 1.0
 * @ClassName Demo23
 * @company 公司
 * @Description StringTable_intern_1.6详解
 * @createTime 2022年08月16日 22:12:12
 */
public class Demo23 {

    // ["a", "b", "ab"]
    public static void main(String[] args) {


        String s = new String("a") + new String("b");

        // 堆  new String("a")   new String("b")  new String("ab")
        String s2 = s.intern(); // 将这个字符串对象尝试放入串池，如果有则并不会放入，如果没有则放入串池， 会把串池中的对象返回
        // s 拷贝一份，放入串池

        String x = "ab";
        System.out.println( s2 == x);
        System.out.println( s == x );
    }
}
