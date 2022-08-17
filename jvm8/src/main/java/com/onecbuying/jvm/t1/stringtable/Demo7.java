package com.onecbuying.jvm.t1.stringtable;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo7
 * @company 公司
 * @Description
 * 演示 StringTable 垃圾回收
 * -Xmx10m -XX:+PrintStringTableStatistics -XX:+PrintGCDetails -verbose:gc
 * @createTime 2022年08月17日 19:43:43
 */
public class Demo7 {
    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        try {
            for (int j = 0; j < 100000; j++) { // j=100, j=10000
                String.valueOf(j).intern();
                i++;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.println(i);
        }

    }
}
