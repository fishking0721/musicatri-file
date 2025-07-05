package org.fishking0721.oss;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableAspectJAutoProxy
@Slf4j
public class FileServerApplication {

    public static void main(String[] args) {
        // 加载 .env 到系统属性
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));

        ConfigurableEnvironment env = SpringApplication.run(FileServerApplication.class, args).getEnvironment();
        log.info(
                "\n--------------------------------------------------------------------------\n\t" +
                        "__  ___           _            __       _       ____________    ______\n" +
                        "   /  |/  /_  _______(_)________ _/ /______(_)     / ____/  _/ /   / ____/\n" +
                        "  / /|_/ / / / / ___/ / ___/ __ `/ __/ ___/ /_____/ /_   / // /   / __/   \n" +
                        " / /  / / /_/ (__  ) / /__/ /_/ / /_/ /  / /_____/ __/ _/ // /___/ /___   \n" +
                        "/_/  /_/\\__,_/____/_/\\___/\\__,_/\\__/_/  /_/     /_/   /___/_____/_____/   \n" +
                        "                                                                          \n\t" +
                        "Application: {} launched successfully! \n\t" +
                        "Local URL: \thttp://localhost:{}\n\t" +
                        "Document:\thttp://localhost:{}/doc.html\n" +
                        "--------------------------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                env.getProperty("server.port"));
    }
}
