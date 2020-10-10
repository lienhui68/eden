package com.eh.eden.concurrent.demo;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/08
 */
public class Demo2 {
    static int i = 1;
    public static void main(String[] args) {
        try {
            if (i == 1) {
                throw new RuntimeException();
            }
        } finally {
            System.out.println("hello");
        }
    }
}
