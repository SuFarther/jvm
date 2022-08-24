package com.onecbuying.jvm.t3.load;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Load9
 * @company 公司
 * @Description TODO
 * @createTime 2022年08月24日 13:52:52
 */
public class Load9 {
    public static void main(String[] args) {
//        Singleton.test();
        Singleton.getInstance();
    }

}

class Singleton {

    public static void test() {
        System.out.println("test");
    }

    private Singleton() {}

    private static class LazyHolder{
        private static final Singleton SINGLETON = new Singleton();
        static {
            System.out.println("lazy holder init");
        }
    }

    public static Singleton getInstance() {
        return LazyHolder.SINGLETON;
    }
}
