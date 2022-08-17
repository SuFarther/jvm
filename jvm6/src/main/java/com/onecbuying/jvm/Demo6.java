package com.onecbuying.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo6
 * @company 公司
 * @Description 演示 StringTable 位置
 * 在jdk8下设置 -Xmx10m -XX:-UseGCOverheadLimit
 * 在jdk6下设置 -XX:MaxPermSize=10m
 * JDK1.6(StringTable串池)用的是永久代PemGen
 * @createTime 2022年08月16日 22:12:12
 */
public class Demo6 {
    public static void main(String[] args) throws InterruptedException {
        List<String> list = new ArrayList<String>();
        int i = 0;
        try {
            for (int j = 0; j < 260000; j++) {
                list.add(String.valueOf(j).intern());
                i++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.println(i);
        }
    }
}
