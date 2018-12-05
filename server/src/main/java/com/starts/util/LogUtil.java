package com.starts.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Jackie on 2018/10/29.
 */
public class LogUtil {

    static public final Logger debug= LogManager.getLogger("Debug");

    static public final Logger biz = LogManager.getLogger("Biz");

    static public final Logger seed = LogManager.getLogger("Seed");

    static public final Logger exception = LogManager.getLogger("Exception");

    public static String getErrorInfoFromException(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sw.close();
            pw.close();

            return "\r\n" + sw.toString() + "\r\n";

        } catch (Exception e2) {
            return "ErrorInfoFromException";
        }

    }

    static public void exception(Exception e){

        exception.error(LogUtil.getErrorInfoFromException(e));
    }

}
