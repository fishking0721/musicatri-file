package org.example.oss.config;

public class Config {
    public static String SecondsToMinutes(double totalSeconds) {

        // 计算分钟和秒数
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);

        // 返回“分加秒”的字符串表示
        return String.format("%dmin%ds", minutes, seconds);
    }
}
