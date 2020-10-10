package com.eh.eden.java8.demo;

import java.util.concurrent.RecursiveTask;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/12
 */
public class ForkJoinSumCalculator extends RecursiveTask<Long> {

    private final long[] numbers;
    private final int start;
    private final int end;

    // 不再将任务分 解为子任务的 组大小
    public static final long THRESHOLD = 10_000;

    // 公共构造 函数用于 创建主任务
    public ForkJoinSumCalculator(long[] numbers) {
        this(numbers, 0, numbers.length);
    }

    // 私有构造函数用于以递归方式为主任务创建子任务
    private ForkJoinSumCalculator(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start; // 无需+1， 计算子任务时左闭右开，不包括end
        if (length <= THRESHOLD) {
            return computeSequentially();
        }
        // 创建一个子任 务来为数组的 前一半求和
        ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start, start + length / 2);
        // 使用另一个ForkJoinPool中的线程异步执行新创建的子任务
        leftTask.fork();
        // 创建一个子任 务来为数组的 后一半求和
        ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start + length / 2, end);
        // 同步执行第二个子 任务，有可能允许进一步递归划分
        Long rightResult = rightTask.compute();
        // 读取第一个子任务的结果， 如果尚未完成就等待
        Long leftResult = leftTask.join();
        // 该任务的结果是两个子任务结果的组合
        return leftResult + rightResult;
    }

    // 在子任务不再可分时计算结果的简单算法
    private long computeSequentially() {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += numbers[i];
        }
        return sum;
    }
}
