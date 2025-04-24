package org.fishking0721.oss.config;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Config {

    public String secondsToMinutes(double totalSeconds) {
        int minutes = (int) (totalSeconds / 60);
        int seconds = (int) (totalSeconds % 60);
        // 返回“分加秒”的字符串表示
        return String.format("%dmin%ds", minutes, seconds);
    }

    public String FileNameEncode(String fileName) {
        return URLEncoder.encode(fileName, StandardCharsets.UTF_8);
    }

    public String getMatcher(String regex, String source) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    } 

}
