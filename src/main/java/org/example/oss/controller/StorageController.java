package org.example.oss.controller;

import org.example.oss.config.AuditLog;
import org.example.oss.config.Config;
import org.example.oss.exception.StorageException;
import org.example.oss.model.ApiResponse;
import org.example.oss.model.ObjectMetadata;
import org.example.oss.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/file")
public class StorageController {
    @Autowired
    private StorageService storageService;

    @AuditLog(operation = "FILE_UPLOAD")
    @PostMapping
    public ResponseEntity<ApiResponse<?>> uploads(@RequestParam("file") MultipartFile[] files) {
        try {
            ObjectMetadata[] metadatas = new ObjectMetadata[files.length];
            for (int i = 0; i < files.length; i++) {
                metadatas[i] = storageService.store(files[i]);
            }
            return ResponseEntity.ok(ApiResponse.success(metadatas));
        } catch (IOException e) {
            throw new StorageException("File upload failed", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        try {
            Resource resource = storageService.load(id);
            //既然是响应头不支持非ASCII字符(中日文字符都不行),那就直接把文件名转utf-8编码之后再回传
            String encoderName = Config.FileNameEencode(resource.getFilename());
    //        System.out.println(resource.getFilename());
    //        System.out.println(encoderName);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
    //                        "attachment; filename=\"" + resource.getFilename() + "\"")
                            "attachment; filename=\"" + encoderName + "\"")
                    .body(resource);
        }catch (IOException e){
            throw new StorageException("File download failed", e);
        }
    }
    @GetMapping("img/{id}")
    public ResponseEntity<Resource> downloadimg(@PathVariable Long id) {
        try {
            Resource resource = storageService.loadimg(id);
            String encoderName = Config.FileNameEencode(resource.getFilename());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + encoderName + "\"")
                    .body(resource);
        }catch (IOException e){
            throw new StorageException("File download failed", e);
        }
    }
    @GetMapping("detail/{id}")
    public ResponseEntity<ApiResponse<?>> detail(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(storageService.detail(id)));
        }catch (Exception e){
            throw new StorageException("File not exist", e);
        }
    }
    @PostMapping("update/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id,@RequestBody ObjectMetadata req) {
        try {
            return ResponseEntity.ok(ApiResponse.success(storageService.update(id,req)));
        }catch (Exception e){
            throw new StorageException("File update failed", e);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            storageService.delete(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new StorageException("File deletion failed", e);
        }
    }
}
