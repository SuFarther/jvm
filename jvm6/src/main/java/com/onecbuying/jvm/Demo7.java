package com.onecbuying.jvm;

/**
 * @author 苏东坡
 * @version 1.0
 * @ClassName Demo7
 * @company 公司
 * @Description 演示 StringTable 垃圾回收
 * -XX:MaxPermSize=10m -XX:+PrintStringTableStatistics -XX:+PrintGCDetails -verbose:gc
 * @createTime 2022年08月16日 22:13:13
 */
public class Demo7 {
    // 1754
    public static void main(String[] args) throws InterruptedException {
        int i = 0;
        try {
            for (int j = 0; j < 500000; j++) { // j=10, j=1000000
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
