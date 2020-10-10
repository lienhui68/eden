package com.eh.eden.java8.demo;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.IntStream;

/**
 * 自定义收集器来对前n个数进行分组，分组条件是判断是否是质数
 *
 * @author David Li
 * @create 2020/08/10
 */
public class PrimeNumbersCollector implements Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> {

    /**
     * supplier方法会返回一个在调用 时创建累加器的函数
     *
     * @return
     */
    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return () -> new HashMap<Boolean, List<Integer>>() {
            {
                put(true, new ArrayList<>());
                put(false, new ArrayList<>());
            }
        };
    }

    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        /**
         * 收集器中最重要的方法是accumulator，因 为它定义了如何收集流中元素的逻辑。
         * 这里它也是实现前面所讲的优化的关键。现在在任何一次迭代中，都可以访问收集过程的部分结果，也就是包含迄今找到的质数的累加器
         * acc.get(true)返回上一次迭代中收集到的质数
         * isPrime(acc.get(true), candidate) 判断当前数是否是质数
         * acc.get(isPrime(acc.get(true), candidate)).add(candidate); 添加对对应的List中
         */
        return (acc, candidate) -> acc.get(isPrime(acc.get(true), candidate)).add(candidate);
    }

    /**
     * 在并行收集时把两个部分累加器合并起来，这里，它只需要合并两个Map，
     * 即 将第二个Map中质数和非质数列表中的所有数字合并到第一个Map的对应列表中就行了：
     * 请注意，实际上这个收集器是不能并行使用的，因为该算法本身是顺序的。这意味着永远都 不会调用 combiner 方法，
     * 你可以把它的实现留空（更好的做法是抛出一个 Unsupported- OperationException异常）。
     * 为了让这个例子完整，还是决定实现它。
     *
     * @return
     */
    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return (m1, m2) -> {
            m1.get(true).addAll(m2.get(true));
            m1.get(false).addAll(m2.get(false));
            return m1;
        };
    }

    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        // 恒等函数
        return Function.identity();
    }

    /**
     * 就characteristics方法而言，我们已经说过，它既不是CONCURRENT也不是UNORDERED， 但却是IDENTITY_FINISH的
     */
    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.IDENTITY_FINISH);
    }

    /**
     * 使用质数作为除数 来判断一个数是否是质数 (非质数本身就不是质数无需判断)
     *
     * @param primes    在添加candidate之前的那个集合
     * @param candidate
     * @return
     */
    public static boolean isPrime(List<Integer> primes, int candidate) {
        int candidateRoot = (int) Math.sqrt((double) candidate);
        return takeWhile(primes, i -> i <= candidateRoot).stream()
                .noneMatch(i -> candidate % i == 0);
    }

    /**
     * 仅仅用小于被测数平方根的质数来测试
     *
     * @param list
     * @param p
     * @param <A>
     * @return
     */
    public static <A> List<A> takeWhile(List<A> list, Predicate<A> p) {
        int i = 0;
        for (A a : list) {
            if (!p.test(a)) {
                return list.subList(0, i);
            }
            i++;
        }
        return list;
    }

    /**
     * 测试质数收集器
     *
     * @param args
     */
    public static void main(String[] args) {
        int n = 20;
        Map<Boolean, List<Integer>> res = IntStream.rangeClosed(2, n)
                .boxed()
                .collect(new PrimeNumbersCollector());
        res.forEach((k, v) -> System.out.println(k + ": " + v));
    }

}