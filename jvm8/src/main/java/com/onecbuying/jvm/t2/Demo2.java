package com.onecbuying.jvm.t2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @ClassName Demo2
 * @company 公司
 * @Description p51  判断垃圾_可达分析_根对象 演示GC Roots
 * @createTime 2022年08月20日 21:58:58
 */
public class Demo2 {
    public static void main(String[] args) throws InterruptedException, IOException {
            List<Object> list1 = new ArrayList<>();
            list1.add("a");
            list1.add("b");
            System.out.println(1);
            System.in.read();

            list1 = null;
            System.out.println(2);
            System.in.read();
            System.out.println("end...");
    }
}
