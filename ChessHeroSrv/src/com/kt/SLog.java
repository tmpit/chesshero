package com.kt;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Toshko
 * Date: 11/7/13
 * Time: 12:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class SLog {
    private final static Logger logger = Logger.getLogger("SLog");
    {
        logger.setLevel((Config.DEBUG ? Level.ALL : Level.OFF));
    }

    public static void write(String str) {
        logger.info(str);
    }

    public static void write(Object obj) {
        logger.info(obj.toString());
    }

    public static void write(boolean b) {
        logger.info("" + b);
    }

    public static void write(int i) {
        logger.info("" + i);
    }

    public static void write(float f) {
        logger.info("" + f);
    }

    public static void write(double d) {
        logger.info("" + d);
    }

    public static void write(long l) {
        logger.info("" + l);
    }

    public static void write(char c) {
        logger.info("" + c);
    }

    public static void write(char c[]) {
        String str = new String(c);
        logger.info(str);
    }
}
