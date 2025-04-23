package org.fishking0721.oss.service;

import cn.hutool.core.lang.Snowflake;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.fishking0721.oss.config.Config;
import org.fishking0721.oss.client.NeteaseApi;
import org.fishking0721.oss.pojo.model.DownloadTask;
import org.fishking0721.oss.pojo.model.ObjectMetadata;
import org.fishking0721.oss.repository.DownloadTaskRepository;
import org.fishking0721.oss.repository.ObjectMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class DownloadService {

    @Autowired
    private DownloadTaskRepository taskRepository;

    @Autowired
    private AsyncTaskService asyncTaskService;

    private Config config = new Config();

    // 创建任务并触发异步下载
//    @Transactional
    public Long createDownloadTask(String url, String source) {
//        source :BV or 163
        // 生成唯一文件名（使用雪花ID）
        Long snowId = new Snowflake().nextId();
        DownloadTask task = new DownloadTask();
        task.setId(snowId);
        task.setUrl(url);
        task.setStatus("PENDING");
        task.setCreatedAt(LocalDateTime.now());
        taskRepository.save(task);

        //下载
        if (Objects.equals(source, "163")) {
            // 163下载
            String songid =  config.getMatcher("id=([^&]+)", url);
            asyncTaskService.downloadNeteasemusic(task.getId(), songid);
        } else if (Objects.equals(source, "BiLi")) {
            // b站下载
            asyncTaskService.downloadVideoAsync(task.getId(), url);
        }else {
            throw new IllegalArgumentException("Invalid source: " + source);
        }
        return task.getId();
    }

    public DownloadTask getTask(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow();
    }


}
