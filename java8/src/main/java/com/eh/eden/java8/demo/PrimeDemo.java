package com.eh.eden.java8.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/10
 */
public class PrimeDemo {
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
                .collect(
                        () -> new HashMap<Boolean, List<Integer>>() {
                            {
                                put(true, new ArrayList<>());
                                put(false, new ArrayList<>());
                            }
                        },
                        (acc, candidate) -> acc.get(isPrime(acc.get(true), candidate)).add(candidate),
                        (m1, m2) -> {
                            m1.get(true).addAll(m2.get(true));
                            m1.get(false).addAll(m2.get(false));
                        }
                );
        res.forEach((k, v) -> System.out.println(k + ": " + v));


    }
}
