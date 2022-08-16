package com.onecbuying.jvm.t1.heap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo13
 * @company 公司
 * @Description 演示查看对象个数 堆转储 dump
 * @createTime 2022年08月16日 16:19:19
 */
public class Demo13 {
    public static void main(String[] args) throws InterruptedException {
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            students.add(new Student());
//            Student student = new Student();
        }
        Thread.sleep(1000000000L);
    }
}
class Student {
    private byte[] big = new byte[1024*1024];
}
