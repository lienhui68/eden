package com.eh.eden.java8.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/06
 */
public class Demo3 {
    public static void main(String[] args) throws IOException {
        try (FileInputStream fis = new FileInputStream(new File("/Users/david/tmp/student.txt"))) {
            byte[] bs = new byte[100];
            fis.read(bs);
        }

    }


}
