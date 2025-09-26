package org.fishking0721.oss.service.strategy;

import org.springframework.stereotype.Component;

@Component
public class NeteaseNewDownloadStrategy extends CommonDownloadStrategy{
    @Override
    public String getType() {
        return "163:audio";
    }

}
