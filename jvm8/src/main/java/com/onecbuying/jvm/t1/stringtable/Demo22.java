package com.onecbuying.jvm.t1.stringtable;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo22
 * @company 公司
 * @Description 搞懂字节码的问题
 * StringTable [ "a", "b" ,"ab" ]  hashtable 结构，不能扩容
 * @createTime 2022年08月16日 22:49:49
 */
public class Demo22 {


    public static void main(String[] args) {
        /*
         **
         * @description:  常量池中的信息，都会被加载到运行时常量池中， 这时 a b ab 都是常量池中的符号，还没有变为 java 字符串对象
         * ldc #2 会把 a 符号变为 "a" 字符串对象
         * ldc #3 会把 b 符号变为 "b" 字符串对象
         * ldc #4 会把 ab 符号变为 "ab" 字符串对象
         *  String s1 = "a";       懒惰的
         *  String s4 = s1 + s2;   new StringBuilder().append("a").append("b").toString()  new String("ab")
         *  String s5 = "a" + "b";  在编译期间的优化，结果已经在编译期确定为ab
         * 1、所有字符串遇到谁都是懒惰的，没有或者遇不到就不把字符串对象创建出来。2、创建完字符串对象会把它放到串池,先到串池找，如果串池中没有，
         * 就会把它放入串池。假如串池中有，就会使用串池中的对象，串池中的字符串对象只会存在一份。或者说不同的字符串对象在串池中是唯一的
         * @param: args
         * @return: void
         * @author 苏东坡
         * @date: 2022/8/16 10:50 PM
         */

        String s1 = "a";
        String s2 = "b";
        String s3 = "ab";
        String s4 = s1 + s2;
        String s5 = "a" + "b";

        System.out.println(s3 == s5);



    }
}
