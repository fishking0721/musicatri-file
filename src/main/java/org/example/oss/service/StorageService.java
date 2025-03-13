package org.example.oss.service;
import org.example.oss.model.ObjectMetadata;
import org.example.oss.repository.ObjectMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class StorageService {
    @Value("${storage.location}")
    private String storagePath;

    @Autowired
    private ObjectMetadataRepository metadataRepository;

    public ObjectMetadata store(MultipartFile file) throws IOException {
        // 生成唯一文件名
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(storagePath, fileName);

        // 保存文件
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // 保存元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setFileName(file.getOriginalFilename());
        metadata.setFilePath(filePath.toString());
        metadata.setContentType(file.getContentType());
        metadata.setFileSize(file.getSize());
        metadata.setUploadTime(LocalDateTime.now());

        return metadataRepository.save(metadata);
    }

    public Resource load(Long fileId) throws IOException{
        ObjectMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        Path filePath = Paths.get(metadata.getFilePath());
        return new FileSystemResource(filePath);
    }

    public void delete(Long fileId) throws IOException {
        ObjectMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        Files.deleteIfExists(Paths.get(metadata.getFilePath()));
        metadataRepository.delete(metadata);
    }
}