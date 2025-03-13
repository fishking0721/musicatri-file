package org.example.oss.controller;

import org.example.oss.exception.StorageException;
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
@RequestMapping("/file")
public class StorageController {
    @Autowired
    private StorageService storageService;

    @PostMapping
    public ResponseEntity<ObjectMetadata> upload(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(storageService.store(file));
        } catch (IOException e) {
            throw new StorageException("File upload failed", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        Resource resource = storageService.load(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            storageService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            throw new StorageException("File deletion failed", e);
        }
    }
}
