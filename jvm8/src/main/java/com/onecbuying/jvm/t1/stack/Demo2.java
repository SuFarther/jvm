package com.onecbuying.jvm.t1.stack;



/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo2
 * @company 公司
 * @Description 演示栈内存溢出 -Xss256k
 * java.lang.StackOverflowError
 * @createTime 2022年08月14日 16:24:24
 */
public class Demo2 {
    private static  int count;

    public static void main(String[] args) {
       try{
           method1();
       }catch(Throwable e){
           e.printStackTrace();
           System.out.println(count);
       }
    }

    private  static  void method1(){
        count++;
        method1();
    }

}
