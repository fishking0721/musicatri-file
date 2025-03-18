package org.example.oss.controller;

import org.example.oss.exception.DownloadException;
import org.example.oss.model.DownloadTask;
import org.example.oss.service.DownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/file/download")
public class DownloadController {

    @Autowired
    private DownloadService downloadService;

    @PostMapping
    public ResponseEntity<?> createDownloadTask(@RequestBody Map<String, String> request){
        try {
            Long taskId = downloadService.createDownloadTask(request.get("url"));
            return ResponseEntity.ok(Map.of("taskId", taskId));
        } catch (Exception e) {
            throw new DownloadException("Failed to create download task", e);
        }
    }

    @GetMapping("/{taskId}/status")
    public ResponseEntity<?> getTaskStatus(@PathVariable Long taskId) throws DownloadException{
        DownloadTask task = downloadService.getTask(taskId);
        return ResponseEntity.ok(Map.of("status", task.getStatus()));
    }
}