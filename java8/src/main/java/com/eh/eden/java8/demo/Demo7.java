package com.eh.eden.java8.demo;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Function;
import java.util.stream.LongStream;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/11
 */
public class Demo7 {
    public static void main(String[] args) {
//        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "1");
        Demo7 test = new Demo7();
        System.out.println("sum done in:" + test.measureSumPerf(Demo7::forkJoinSum, 100_000_000L));
    }

    public long measureSumPerf(Function<Long, Long> adder, long n) {
        long fastest = Long.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            long start = System.nanoTime();
            adder.apply(n);
            long duration = (System.nanoTime() - start) / 1_000_000;
            System.out.println(i + ": " + duration);
            if (duration < fastest) fastest = duration;
        }
        return fastest;
    }

    public static long forkJoinSum(long n) {
        long[] numbers = LongStream.rangeClosed(0, n).toArray();
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
        return new ForkJoinPool().invoke(task);
    }
}
