package com.eh.eden.java8.demo;


import java.time.*;

/**
 * todo
 *
 * @author David Li
 * @create 2020/08/14
 */
public class TimeDemo {
    public static void main(String[] args) {
        ZoneId romeZone = ZoneId.of("Europe/Rome");
        LocalDate date = LocalDate.now();
        ZonedDateTime zdt1 = date.atStartOfDay(romeZone);

        LocalDateTime dateTime = LocalDateTime.now();
        ZonedDateTime zdt2 = dateTime.atZone(romeZone);

        Instant instant = Instant.now();
        ZonedDateTime zdt3 = instant.atZone(romeZone);

        System.out.println();


    }
}

