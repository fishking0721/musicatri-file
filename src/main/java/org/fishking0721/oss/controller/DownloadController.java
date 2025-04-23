package org.fishking0721.oss.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.fishking0721.oss.exception.DownloadException;
import org.fishking0721.oss.pojo.model.DownloadTask;
import org.fishking0721.oss.pojo.model.ApiResponse;
import org.fishking0721.oss.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/file/download")
public class DownloadController {

    @Autowired
    private DownloadService downloadService;

    @PostMapping
    @Operation(summary = "请求下载b站和油管音频")
    public ResponseEntity<ApiResponse<DownloadTask>> createDownloadTask(@RequestBody Map<String, String> request){
            Long taskId = downloadService.createDownloadTask(request.get("url"),request.get("source"));
            DownloadTask task = downloadService.getTask(taskId);
            return ResponseEntity.ok(ApiResponse.success(task));
    }

    @GetMapping("/{taskId}/status")
    @Operation(summary = "获取下载任务状态")
    public ResponseEntity<ApiResponse<?>> getTaskStatus(@PathVariable Long taskId) throws DownloadException{
        DownloadTask task = downloadService.getTask(taskId);
        return ResponseEntity.ok(ApiResponse.success(task));
    }
}