package org.fishking0721.oss.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class YoutubeDownloadStrategy extends CommonDownloadStrategy {
    @Override
    public String getType() {
        return "youtube:audio";
    }
}
