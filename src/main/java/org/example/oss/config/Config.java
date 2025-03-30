package org.example.oss.config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Config {
    public static String SecondsToMinutes(double totalSeconds) {
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);
        // 返回“分加秒”的字符串表示
        return String.format("%dmin%ds", minutes, seconds);
    }

    public static String FileNameEencode(String fileName) {
        try {
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getMatcher(String regex, String source) {
        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            result = matcher.group();
        }
        return result;
    } 

}
