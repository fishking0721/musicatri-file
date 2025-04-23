package org.example.oss.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.oss.config.AuditLog;
import org.example.oss.config.Config;
import org.example.oss.exception.StorageException;
import org.example.oss.pojo.model.ApiResponse;
import org.example.oss.pojo.model.ObjectMetadata;
import org.example.oss.pojo.dto.MetadataPaginatedQueryDTO;
import org.example.oss.service.StorageService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;

import java.io.IOException;

@RestController
@RequestMapping("api/v1/file")
@Tag(name = "存储管理视图", description = "提供管理存储文件相关接口")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping
    @AuditLog(operation = "FILE_UPLOAD")
    @Operation(summary = "上传音频")
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
    @Operation(summary = "下载音频")
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
    @Operation(summary = "下载音频封面图")
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
    @Operation(summary = "查询音频详情")
    public ResponseEntity<ApiResponse<?>> detail(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(ApiResponse.success(storageService.detail(id)));
        }catch (Exception e){
            throw new StorageException("File not exist", e);
        }
    }
    @GetMapping("/viewall")
    @Operation(summary = "分页查询")
    public ResponseEntity<ApiResponse<?>> simpleview(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(ApiResponse.success(storageService.simpleview(page,size)));
        }catch (Exception e){
            throw new StorageException("File not exist", e);
        }
    }

    @GetMapping("/viewall/filter")
    @Operation(
            summary = "过滤分页查询",
            description = "基于/viewall接口，此接口拓展了查询参数，支持使用*进行模糊查询"
    )
    public ResponseEntity<ApiResponse<?>> filteredPaginatedView(
            @ParameterObject @ModelAttribute MetadataPaginatedQueryDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(storageService.filteredPaginatedView(dto)));
    }

    @PostMapping("update/{id}")
    @Operation(summary = "更新音乐文件信息")
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable Long id,
            @RequestBody ObjectMetadata req) {
        try {
            return ResponseEntity.ok(ApiResponse.success(storageService.update(id,req)));
        }catch (Exception e){
            throw new StorageException("File update failed", e);
        }
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "删除指定音乐文件")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            storageService.delete(id);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new StorageException("File deletion failed", e);
        }
    }
}
