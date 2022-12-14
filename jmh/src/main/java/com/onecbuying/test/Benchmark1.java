package com.onecbuying.test;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @version 1.0
 * @ClassName Benchmark1
 * @company 公司
 * @Description p161(运行期优化-字段优化)
 * @createTime 2022年08月24日 15:10:10
 */
//@Warmup(iterations = 2, time = 1)
//@Measurement(iterations = 5, time = 1)
//@State(Scope.Benchmark)
public class Benchmark1 {

    int[] elements = randomInts(1_000);

    private static int[] randomInts(int size) {
        Random random = ThreadLocalRandom.current();
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = random.nextInt();
        }
        return values;
    }

//    @Benchmark
    public void test1() {
        for (int i = 0; i < elements.length; i++) {
            doSum(elements[i]);
        }
    }

//    @Benchmark
    public void test2() {
        int[] local = this.elements;
        for (int i = 0; i < local.length; i++) {
            doSum(local[i]);
        }
    }

//    @Benchmark
    public void test3() {
        for (int element : elements) {
            doSum(element);
        }
    }

    static int sum = 0;

//    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    static void doSum(int x) {
        sum += x;
    }


//    public static void main(String[] args) throws RunnerException {
//        Options opt = new OptionsBuilder()
//                .include(Benchmark1.class.getSimpleName())
//                .forks(1)
//                .build();
//
//        new Runner(opt).run();
//    }
}

