package org.fishking0721.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "yt-dlp")
public class YtDlpProperties {

    private String location;

}
