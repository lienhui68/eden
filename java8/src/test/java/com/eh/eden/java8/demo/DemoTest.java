package com.eh.eden.java8.demo;

import com.google.common.collect.Lists;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/09
 */
public class DemoTest {

    /**
     * 无限流
     * iterate
     */
    @Test
    public void test1() {
        IntStream.iterate(0, n -> n + 2).forEach(System.out::println);
    }

    /**
     * 打印斐波拉契数列
     * iterate
     */
    @Test
    public void test2() {
        int n = 20;
        Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
                .map(t -> t[0])
                .limit(n)
                .forEach(d -> System.out.printf("%d  ", d));
    }

    /**
     * 构建斐波拉契数列
     * 使用generate方式
     */
    @Test
    public void test3() {
        IntSupplier supplier = new IntSupplier() {
            private int pre = 0;
            private int curr = 1;

            @Override
            public int getAsInt() {
                int old = pre;
                this.pre = curr;
                this.curr = old + this.curr;
                return old;
            }
        };

        IntStream.generate(supplier).limit(20).forEach(System.out::println);

    }

    /**
     * 给定两个数字列表，如何返回所有的数对呢？例如，给定列表[1, 2, 3]和列表[3, 4]，
     * 应该返回[(1, 3), (1, 4), (2, 3), (2, 4), (3, 3), (3, 4)]。为简单起见，
     * 你可以用有两个元素的数组来代表数对。
     */
    @Test
    public void test4() {
        List<Integer> a = Lists.newArrayList(1, 2, 3);
        List<Integer> b = Lists.newArrayList(3, 4);
        a.stream().flatMap(i -> b.stream().map(j -> new int[]{i, j})).forEach(i -> System.out.println(Arrays.toString(i)));
    }

    /**
     * IntStream.rangeClosed()
     */
    @Test
    public void test5() {
        IntStream.rangeClosed(1, 100).filter(i -> i >> 2 == 0).forEach(System.out::println);
    }

    /**
     * 勾股数
     */
    @Test
    public void test6() {
        final int N = 20;
        Stream<int[]> pythagoreanTriples = IntStream.rangeClosed(1, N).boxed()
                .flatMap(a -> IntStream.rangeClosed(a, N)
                        .boxed()
                        .map(b -> new int[]{a, b, (int) Math.sqrt(a * a + b * b)}))
                .filter(t -> t[2] % 1 == 0)
                .filter(t -> t[2] <= N);

        pythagoreanTriples.forEach(t -> System.out.printf("[%d, %d, %d]\n", t[0], t[1], t[2]));
    }

    /**
     * Stream.of
     */
    @Test
    public void test7() {
        Stream<String> ss = Stream.of("hello", "world");
        ss.map(String::toUpperCase).forEach(System.out::println);
    }

    /**
     * Arrays.stream()
     */
    @Test
    public void test8() {
        int[] a = new int[]{1, 2, 3};
        int sum = Arrays.stream(a).sum();
        System.out.println(sum);
    }

    /**
     * 由文件生成流
     * 查看文件中单词总个数
     */
    @Test
    public void test9() {
        long count;
        try (Stream<String> lines = Files.lines(Paths.get("/tmp/com.eh/a.txt"), Charset.defaultCharset())
                .filter(s -> s.trim().length() != 0)
        ) {
            count = lines.flatMap(line -> Arrays.stream(line.trim().split(" "))).distinct().count();
            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    @Test
    public void test10() {
    }

}

