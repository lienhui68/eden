package com.eh.eden.java;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/14
 */
public class SimpleDateFormatTest extends Thread {

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    private String name;
    private String dateStr;

    public SimpleDateFormatTest(String name, String dateStr) {
        this.name = name;
        this.dateStr = dateStr;
    }

    @Override
    public void run() {
        try {
            Date date = df.parse(dateStr);
            System.out.println(name + ": date:" + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            executorService.execute(new SimpleDateFormatTest("thread-" + i, "2017-06-10"));
        }

        executorService.shutdown();

    }
}
