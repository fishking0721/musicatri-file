package org.fishking0721.oss.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.fishking0721.oss.pojo.dto.AudioDownloadTaskCreateDTO;
import org.fishking0721.oss.pojo.model.AudioDownloadTask;
import org.fishking0721.oss.service.AudioDownloadTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

@Slf4j
public abstract class DownloadStrategy {

    @Autowired
    private AudioDownloadTaskService audioDownloadTaskService;

    /**
     * 创建下载任务
     */
    public abstract void execute(AudioDownloadTaskCreateDTO dto) throws Exception;

    /**
     * 标记当前下载策略匹配的类型
     */
    public abstract String getType();

    /**
     * 执行异步逻辑
     */
    @Async
    public void doExecute(AudioDownloadTaskCreateDTO dto) {
        Long taskId = dto.getId();  // 更新状态
        try {
            audioDownloadTaskService.updateDownloadTaskStatusById(taskId, AudioDownloadTask.Status.DOWNLOADING);
            this.execute(dto);
            audioDownloadTaskService.updateDownloadTaskStatusById(taskId, AudioDownloadTask.Status.COMPLETED);
        } catch (Exception e) {
            audioDownloadTaskService.updateDownloadTaskStatusById(taskId, AudioDownloadTask.Status.FAILED);
            log.error(e.getMessage(), e);
        }
    }
}
