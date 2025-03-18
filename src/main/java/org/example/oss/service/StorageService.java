package org.example.oss.service;

import cn.hutool.core.lang.Snowflake;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.example.oss.config.Config;
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

@Service
public class StorageService {
    @Value("${storage.location}")
    private String storagePath;
    @Value("${ffmpeg.ffprobe-path}")
    private String FFprobePath;

    @Autowired
    private ObjectMetadataRepository metadataRepository;

    public ObjectMetadata store(MultipartFile file) throws IOException {
        // 生成唯一文件名
//        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Long Snowid = new Snowflake().nextId();
        String fileName = Snowid + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(storagePath, fileName);

        // 保存文件
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        //FFmpeg获取音频元数据
        FFprobe ffprobe = new FFprobe(FFprobePath);
        FFmpegProbeResult probeResult = ffprobe.probe(filePath.toString());
        FFmpegFormat format = probeResult.getFormat();
//        System.out.println(format.duration);
        // 保存元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setId(Snowid);
        metadata.setMusicLength(Config.SecondsToMinutes(format.duration));
        metadata.setFileName(file.getOriginalFilename());
        metadata.setFilePath(filePath.toString());
        metadata.setContentType(file.getContentType());
        metadata.setFileSize(file.getSize());
        metadata.setUploadTime(LocalDateTime.now());

        return metadataRepository.save(metadata);
    }

    //回传文件
    public Resource load(Long fileId) throws IOException{
        ObjectMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        //不能在这里改变文件名编码
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