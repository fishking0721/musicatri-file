package org.fishking0721.oss.service.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DownloadStrategyFactory {

    // source到策略的映射
    private final Map<String, DownloadStrategy> strategies = new HashMap<>();

    @Autowired
    public DownloadStrategyFactory(List<DownloadStrategy> strategies) {
        strategies.forEach(e -> this.strategies.put(e.getType(), e));
    }

    // 获取策略
    public DownloadStrategy getStrategy(String type) {
        return strategies.get(type);
    }

}
