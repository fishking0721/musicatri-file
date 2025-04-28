package org.fishking0721.oss.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.fishking0721.oss.auth.RequiredRole;
import org.fishking0721.oss.exception.DownloadException;
import org.fishking0721.oss.pojo.dto.AudioDownloadTaskCreateDTO;
import org.fishking0721.oss.pojo.model.ApiResponse;
import org.fishking0721.oss.pojo.vo.AudioDownloadTaskVO;
import org.fishking0721.oss.service.AudioDownloadService;
import org.fishking0721.oss.service.AudioDownloadTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/file/audio")
@Tag(name = "音频下载视图", description = "负责创建音频下载任务")
public class AudioController {

    @Autowired
    private AudioDownloadService audioDownloadService;

    @Autowired
    private AudioDownloadTaskService audioDownloadTaskService;

//    @RequiredRole("user")
    @PostMapping("/download")
    @Operation(summary = "下载目标音频")
    public ResponseEntity<ApiResponse<?>> downloadAudio(@RequestBody AudioDownloadTaskCreateDTO dto) throws Exception {
        AudioDownloadTaskVO vo = audioDownloadService.processDownloadTask(dto);
        return ResponseEntity.ok(ApiResponse.success(vo));
    }

    @GetMapping("/{taskId}/details")
    @Operation(summary = "获取下载任务状态")
    public ResponseEntity<ApiResponse<?>> getTaskDetails(@PathVariable Long taskId) throws DownloadException{
        AudioDownloadTaskVO vo = audioDownloadTaskService.getDownloadTaskById(taskId);
        return ResponseEntity.ok(ApiResponse.success(vo));
    }
}