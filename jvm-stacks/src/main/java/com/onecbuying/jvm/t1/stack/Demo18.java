package com.onecbuying.jvm.t1.stack;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo18
 * @company 公司
 * @Description  局部变量的线程安全
 * @createTime 2022年08月14日 12:16:16
 */
public class Demo18 {

    /*
    **
     * @description:  多个线程同时执行此方法
     * @param:
     * @return: void
     * @author 苏东坡
     * @date: 2022/8/14 12:17 PM
     */

    static void m1(){
        int x = 0;
        for (int  i = 0; i <5000;i++){
            x++;
        }
        System.out.println(x);
    }
}
