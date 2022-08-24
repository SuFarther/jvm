package com.onecbuying.jvm.t3.load;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Load5_3
 * @company 公司
 * @Description p154(类加载器-双亲委派-源码分析2)
 * @createTime 2022年08月24日 14:18:18
 */
public class Load5_3 {
    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println(Load5_3.class.getClassLoader());
        Class<?> aClass = Load5_3.class.getClassLoader().loadClass("com.onecbuying.jvm.t3.load.H");
        System.out.println(aClass.getClassLoader());

    }
}

