package org.fishking0721.oss.service;

import lombok.extern.slf4j.Slf4j;
import org.fishking0721.oss.pojo.dto.AudioDownloadTaskCreateDTO;
import org.fishking0721.oss.pojo.model.AudioDownloadTask;
import org.fishking0721.oss.pojo.vo.AudioDownloadTaskVO;
import org.fishking0721.oss.service.strategy.DownloadStrategy;
import org.fishking0721.oss.service.strategy.DownloadStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class AudioDownloadService {

    @Autowired
    private AudioDownloadTaskService audioDownloadTaskService;

    @Autowired
    private DownloadStrategyFactory downloadStrategyFactory;

    @Value("${storage.location}")
    private String storagePath;  // 本地仓库路径

    // 创建任务并触发异步下载
    public AudioDownloadTaskVO processDownloadTask(AudioDownloadTaskCreateDTO createDTO) throws Exception {
        Path filePath = Paths.get(storagePath);
        Files.createDirectories(filePath);  // 确保仓库目录存在

        long taskId = audioDownloadTaskService.createDownloadTask(createDTO);  // 创建下载任务
        DownloadStrategy strategy = downloadStrategyFactory.getStrategy(createDTO.getType());

        if (strategy == null) {
            // 未找到策略，将状态变更为Failed
            audioDownloadTaskService.updateDownloadTaskStatusById(taskId, AudioDownloadTask.Status.FAILED);
            throw new RuntimeException("Strategy not found");
        }

        strategy.doExecute(createDTO);  // 异步下载
        return audioDownloadTaskService.getDownloadTaskById(taskId);  // 返回任务视图
    }
}
