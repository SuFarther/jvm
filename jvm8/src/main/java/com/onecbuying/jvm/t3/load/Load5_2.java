package com.onecbuying.jvm.t3.load;

/**
 * @version 1.0
 * @ClassName Load5_2
 * @company 公司
 * @Description p152(类加载器-扩展类加载器)
 * 在 C:\Program Files\Java\jdk1.8.0_91 下有一个 my.jar
 * 里面也有一个 G 的类，观察到底是哪个类被加载了
 * @createTime 2022年08月24日 14:16:16
 */
public class Load5_2 {
    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> aClass = Class.forName("com.onecbuying.jvm.t3.load.G");
        System.out.println(aClass.getClassLoader());
    }
}

