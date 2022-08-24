package com.onecbuying.jvm.t3.jit;

/**
 * @version 1.0
 * @ClassName JIT1
 * @company 公司
 * @Description p159(运行期优化-逃逸分析)
 * @createTime 2022年08月24日 14:42:42
 */
public class JIT1 {
    // -XX:+PrintCompilation -XX:-DoEscapeAnalysis
    public static void main(String[] args) {
        for (int i = 0; i < 200; i++) {
            long start = System.nanoTime();
            for (int j = 0; j < 1000; j++) {
                new Object();
            }
            long end = System.nanoTime();
            System.out.printf("%d\t%d\n",i,(end - start));
        }
    }
}
