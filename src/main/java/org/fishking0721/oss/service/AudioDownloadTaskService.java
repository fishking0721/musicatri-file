package org.fishking0721.oss.service;

import cn.hutool.core.lang.Snowflake;
import org.fishking0721.oss.pojo.dto.AudioDownloadTaskCreateDTO;
import org.fishking0721.oss.pojo.dto.AudioDownloadTaskDTO;
import org.fishking0721.oss.pojo.model.AudioDownloadTask;
import org.fishking0721.oss.pojo.vo.AudioDownloadTaskVO;
import org.fishking0721.oss.repository.AudioDownloadTaskRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@SuppressWarnings("UnusedReturnValue")
public class AudioDownloadTaskService {

    @Autowired
    private AudioDownloadTaskRepository audioDownloadTaskRepository;

    @Autowired
    private Snowflake snowflake;

    public AudioDownloadTaskVO getDownloadTaskById(Long taskId) {
        AudioDownloadTask entity = audioDownloadTaskRepository.findById(taskId).orElseThrow();
        return AudioDownloadTaskVO.of(entity);
    }

    public long createDownloadTask(AudioDownloadTaskCreateDTO dto) {
        long taskId = snowflake.nextId();
        AudioDownloadTask task = AudioDownloadTask.builder()
                .id(taskId)
                .url(dto.getUrl())
                .status(AudioDownloadTask.Status.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        audioDownloadTaskRepository.save(task);
        dto.setId(taskId);  // 填充taskId
        return taskId;
    }

    public boolean saveDownloadTask(AudioDownloadTaskDTO dto) {
        AudioDownloadTask entity = new AudioDownloadTask();
        BeanUtils.copyProperties(dto, entity);
        audioDownloadTaskRepository.save(entity);
        return true;
    }

    public boolean updateDownloadTaskStatusById(Long taskId, AudioDownloadTask.Status status) {
        AudioDownloadTask task = audioDownloadTaskRepository.findById(taskId).orElseThrow();
        task.setStatus(status);
        audioDownloadTaskRepository.save(task);
        return true;
    }

    public boolean updateDownloadTask(AudioDownloadTaskDTO dto) {
        Optional<AudioDownloadTask> optionalTask = audioDownloadTaskRepository.findById(dto.getId());
        if (optionalTask.isEmpty()) {
            return false;
        }

        AudioDownloadTask task = optionalTask.get();  // 仅更新非空字段
        if (dto.getUrl() != null) {
            task.setUrl(dto.getUrl());
        }

        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        }

        if (dto.getErrorMessage() != null) {
            task.setErrorMessage(dto.getErrorMessage());
        }

        audioDownloadTaskRepository.save(task); // 持久化更新
        return true;
    }

}
