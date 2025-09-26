package org.fishking0721.oss.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
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

    public String NodetoString(JsonNode nodetext) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < nodetext.size(); i++) {
                sb.append(nodetext.get(i).asText()).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            return sb.toString();
        }catch (Exception e){
            return (e + "error");
        }
    }

}
