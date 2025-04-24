package org.fishking0721.oss.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class BilibiliDownloadStrategy extends CommonDownloadStrategy {
    @Override
    public String getType() {
        return "bilibili:audio";
    }
}
