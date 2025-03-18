package org.example.oss.config;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
}
