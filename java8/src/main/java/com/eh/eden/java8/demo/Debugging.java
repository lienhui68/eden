package com.eh.eden.java8.demo;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/12
 */
public class Debugging {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(2, 3, 4, 5);

        numbers.stream()
                .peek(x -> System.out.println("from stream: " + x))
                .map(x -> x + 17)
                .peek(x -> System.out.println("after map: " + x))
                .filter(x -> x % 2 == 0)
                .peek(x -> System.out.println("after filter: " + x))
                .limit(3)
                .peek(x -> System.out.println("after limit: " + x))
                .forEach(System.out::println);
    }

    public static int divideByZero(int n) {
        return n / 0;
    }
}

@Getter
class Point {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point moveRightBy(int x) {
        return new Point(this.x + x, this.y);
    }

}
