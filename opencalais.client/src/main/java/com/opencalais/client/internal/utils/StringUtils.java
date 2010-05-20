package com.opencalais.client.internal.utils;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: jtoth
 * Date: May 19, 2009
 * Time: 1:33:12 PM
 */
public class StringUtils {

    public static String convertStreamToString(InputStream is) {

        StringBuilder out = new StringBuilder();

        try {
            final char[] buffer = new char[0x10000];
            Reader in = new InputStreamReader(is, "UTF-8");
            int read;
            do {
                read = in.read(buffer, 0, buffer.length);
                if (read > 0) {
                    out.append(buffer, 0, read);
                }
            } while (read >= 0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return out.toString();
    }


}