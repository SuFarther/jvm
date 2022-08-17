package com.onecbuying.jvm.t1.stringtable;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo23
 * @company 公司
 * @Description
 * 常量池中的字符串仅是符号，第一次用到时才变为对象
 * 利用串池的机制，来避免重复创建字符串对象
 * 字符串变量拼接的原理是 StringBuilder (JDK1.8)
 * 字符串常量拼接的原理是编译期优化
 * 可以使用intern 方法，主动将串池中还没有的字符串对象放入串池
 * 1.8 将这个字符串对象尝试放放串池，如果有则并不会放入，如果没有则放入串池，会把串池中的对象返回
 * 1.6 将这个字符串对象尝试放入串池，如果有则并不会放入，如果没有会把对象复制一份，放入串池，会把串池中的对象返回
 * @createTime 2022年08月17日 16:27:27
 */
public class Demo23 {
    //  ["ab", "a", "b"]
    public static void main(String[] args) {

        String x = "ab";
        String s = new String("a") + new String("b");

        // 堆  new String("a")   new String("b") new String("ab")
        String s2 = s.intern(); // 将这个字符串对象尝试放入串池，如果有则并不会放入，如果没有则放入串池， 会把串池中的对象返回

        System.out.println( s2 == x);
        System.out.println( s == x );
    }

}
