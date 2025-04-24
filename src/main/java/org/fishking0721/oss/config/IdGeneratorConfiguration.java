package org.fishking0721.oss.config;

import cn.hutool.core.lang.Snowflake;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfiguration {

    @Bean
    public Snowflake snowflake() {
        return new Snowflake();
    }

}
