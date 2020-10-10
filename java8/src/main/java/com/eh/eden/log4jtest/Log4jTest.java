package com.eh.eden.log4jtest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4jTest {
    private static final Logger logger = LoggerFactory.getLogger(Log4jTest.class);

    public static void main(String[] args) {
        logger.error("info log4j david");
        logger.info("info log4j obama");
        logger.info("info log4j trump");
        logger.info("info log4j david");
        logger.warn("info log4j david");
    }
}
