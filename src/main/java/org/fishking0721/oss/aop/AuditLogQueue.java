package org.fishking0721.oss.aop;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.fishking0721.oss.pojo.model.AuditLogRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
@Slf4j
@Component
public class AuditLogQueue {
    @Value("${auditlog.location}")
    String auditLogLocation;

    private static final BlockingQueue<AuditLogRecord> queue = new LinkedBlockingQueue<>(1000);

    @PostConstruct
    public void initConsumer() {
        new Thread(() -> {
            while (true) {
                try {
                    AuditLogRecord record = queue.take();
                    writeToStorage(record);
                } catch (InterruptedException | IOException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void add(AuditLogRecord record) {
        if (!queue.offer(record)) {
            log.warn("Audit log queue is full, dropping record: {}", record);
        }
    }

    private void writeToStorage(AuditLogRecord record) throws IOException {
        String recordStr = record.toString() + "\n";
        String auditLogfilePath = auditLogLocation + "/audit.log";
//        log.info("[AUDIT] {}", record);
        // 同步写入文件（追加模式）
        try {
            Files.createDirectories(Path.of(auditLogLocation));
            Files.write(Paths.get(auditLogfilePath),
                    recordStr.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Failed to write file", e);
        }
    }
}
