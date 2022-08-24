package com.onecbuying.jvm.t3.load;

import java.io.IOException;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Load2
 * @company 公司
 * @Description 解析的含义
 * @createTime 2022年08月24日 13:14:14
 */
public class Load2 {
    public static void main(String[] args) throws ClassNotFoundException, IOException {
//        ClassLoader classloader = Load2.class.getClassLoader();
//        Class<?> c = classloader.loadClass("com.onecbuying.jvm.t3.load.C");
        new C();
        System.in.read();
    }
}

class C {
    D d = new D();
}

class D {

}
