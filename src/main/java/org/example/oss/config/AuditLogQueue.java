package org.example.oss.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.oss.model.AuditLogRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
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
                } catch (InterruptedException e) {
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

    private void writeToStorage(AuditLogRecord record) {
        String recordStr = record.toString() + "\n";
//        log.info("[AUDIT] {}", record);
        // 同步写入文件（追加模式）
        try {
            Files.write(Paths.get(auditLogLocation),
                    recordStr.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Failed to write file", e);
        }
    }
}
