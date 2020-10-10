package com.eh.eden.utils;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/08
 */
public class BinaryUtil {
    /**
     * 获取整数在内存中二进制形式
     * @param i
     * @return 二进制补码形式
     */
    public static String toBinaryString(int i) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < 32; j++) {
            sb.append(Integer.bitCount(i & (1 << j)));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(toBinaryString(2));
    }
}
