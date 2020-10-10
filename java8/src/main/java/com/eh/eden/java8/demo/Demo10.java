package com.eh.eden.java8.demo;

import java.util.function.Function;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/17
 */
public class Demo10 {
    public static void main(String[] args) {
        System.out.println(repeat(3, (Integer x) -> 2 * x).apply(10));
    }

    static <A, B, C> Function<A, C> compose(Function<B, C> g, Function<A, B> f) {
        return x -> g.apply(f.apply(x));
    }

    static <A> Function<A, A> repeat(int n, Function<A, A> f) {
        return n == 0 ? x -> x : compose(f, repeat(n - 1, f));
    }

}

interface TriFunction<S, T, U, R> {
    R apply(S s, T t, U u);
}



