package com.onecbuying.jvm.t3.load;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Load5_1
 * @company 公司
 * @Description p151(类加载器-启动类加载器)
 * @createTime 2022年08月24日 14:11:11
 */
public class Load5_1 {
    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> aClass = Class.forName("com.onecbuying.jvm.t3.load.F");
        System.out.println(aClass.getClassLoader()); // AppClassLoader  ExtClassLoader
    }
}
