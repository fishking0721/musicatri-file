package org.fishking0721.oss.service;

import cn.hutool.core.lang.Snowflake;
import jakarta.persistence.criteria.Predicate;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegFormat;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.fishking0721.oss.config.FileSecurity;
import org.fishking0721.oss.pojo.model.AudioMetadata;
import org.fishking0721.oss.pojo.dto.AudioMetadataPaginatedQueryDTO;
import org.fishking0721.oss.pojo.model.ThumbnailMetadata;
import org.fishking0721.oss.pojo.vo.AudioMetadataPaginatedQueryVO;
import org.fishking0721.oss.repository.AudioMetadataRepository;
import org.fishking0721.oss.pojo.vo.PageResultVO;
import org.fishking0721.oss.repository.ThumbnailMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class StorageService {
    @Value("${storage.location}")
    private String storagePath;
    @Value("${ffmpeg.ffprobe-path}")
    private String FFprobePath;

    @Autowired
    private AudioMetadataRepository audioMetadataRepository;

    @Autowired
    private ThumbnailMetadataRepository thumbnailMetadataRepository;

    public AudioMetadata store(MultipartFile file) throws IOException {
        // 验证文件类型
        FileSecurity fileSecurity = new FileSecurity();
        fileSecurity.validateFile(file);

        // 生成唯一文件名
        Long Snowid = new Snowflake().nextId();
        //处理文件名
        String fileName = Snowid + "_" + fileSecurity.SafePath(file.getOriginalFilename());
        Path filePath = Paths.get(storagePath, fileName);

        // 保存文件
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        //FFmpeg获取音频元数据
        FFprobe ffprobe = new FFprobe(FFprobePath);
        FFmpegProbeResult probeResult = ffprobe.probe(filePath.toString());
        FFmpegFormat format = probeResult.getFormat();
//        System.out.println(format.duration);
        // 保存元数据
        AudioMetadata metadata = new AudioMetadata();
        metadata.setId(Snowid);
        metadata.setDuration(String.valueOf(format.duration));
        metadata.setFilename(file.getOriginalFilename());
        metadata.setFilepath(filePath.toString());
        metadata.setContentType(file.getContentType());
        metadata.setFilesize(file.getSize());
        metadata.setUploadTime(LocalDateTime.now());

        return audioMetadataRepository.save(metadata);
    }

    //回传文件
    public Resource load(Long fileId) throws IOException{
        AudioMetadata metadata = audioMetadataRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        //不能在这里改变文件名编码
        Path filePath = Paths.get(metadata.getFilepath());
        return new FileSystemResource(filePath);
    }

    public Resource loadimg(Long fileId) throws IOException{
        ThumbnailMetadata metadata = thumbnailMetadataRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        //不能在这里改变文件名编码
        Path imgPath = Paths.get(metadata.getFilepath());
        return new FileSystemResource(imgPath);
    }

    public void delete(Long fileId) throws IOException {
        AudioMetadata metadata = audioMetadataRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        Files.deleteIfExists(Paths.get(metadata.getFilepath()));
        audioMetadataRepository.delete(metadata);
    }

    public Object detail(Long id) {
        return audioMetadataRepository.findById(id).orElse(null);
    }

    public Object update(Long id, AudioMetadata req) throws IOException{
        AudioMetadata metadata = audioMetadataRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found"));
        metadata.setFilename(req.getFilename());
        metadata.setArtist(req.getArtist());
        metadata.setUploaderId(req.getUploaderId());
        metadata.setSourceUrl(req.getSourceUrl());
        return audioMetadataRepository.save(metadata);
    }


    /**
     * 判断字符串值是否有效
     */
    private boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * 具备过滤分页查询能力的视图业务
     * @param dto 查询以及分页相关条件
     * @return 元数据分页查询VO对象
     */
    public PageResultVO<AudioMetadataPaginatedQueryVO> filteredPaginatedView(AudioMetadataPaginatedQueryDTO dto) {
        // JPA分页逻辑从0作为起始
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize());
        Specification<AudioMetadata> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            String artist = dto.getArtist();  // 曲作者
            if (isPresent(artist)) {
                predicates.add(cb.like(root.get("artist"), artist.replace("*", "%")));
            }

            String filename = dto.getFilename();  // 文件名
            if (isPresent(filename)) {
                predicates.add(cb.like(root.get("filename"), filename.replace("*", "%")));
            }

            String contentType = dto.getContentType();  // 文件类型
            if (isPresent(contentType)) {
                predicates.add(cb.equal(root.get("contentType"), contentType));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<AudioMetadata> page = audioMetadataRepository.findAll(specification, pageable);
        List<AudioMetadataPaginatedQueryVO> rows = page.get().map(AudioMetadataPaginatedQueryVO::of).toList();

        PageResultVO<AudioMetadataPaginatedQueryVO> vo = new PageResultVO<>();
        vo.setRows(rows);
        vo.setCount(page.getTotalElements());
        return vo;
    }
}