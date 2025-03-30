package org.example.oss.service;

import cn.hutool.core.lang.Snowflake;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.oss.config.Config;
import org.example.oss.model.DownloadTask;
import org.example.oss.model.ObjectMetadata;
import org.example.oss.repository.DownloadTaskRepository;
import org.example.oss.repository.ObjectMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class DownloadService {
    @Value("${storage.location}")
    private String storagePath;
    @Value("${yt-dlp.location}")
    private String ytDlpPath;

    @Autowired
    private DownloadTaskRepository taskRepository;

    @Autowired
    private ObjectMetadataRepository metadataRepository;

    // 创建任务并触发异步下载
    @Transactional
    public Long createDownloadTask(String url) {
        DownloadTask task = new DownloadTask();
        task.setUrl(url);
        task.setStatus("PENDING");
        task.setCreatedAt(LocalDateTime.now());
        taskRepository.save(task);

        // 异步执行下载
        downloadVideoAsync(task.getId(), url);
        return task.getId();
    }

    // 异步下载核心逻辑
    @Async
    public void downloadVideoAsync(Long taskId, String url) {
        DownloadTask task = taskRepository.findById(taskId).orElseThrow();
        task.setStatus("DOWNLOADING");
        taskRepository.save(task);

        try {
            // 生成唯一文件名（使用雪花ID）
            Long snowId = new Snowflake().nextId();
            Path filePath = Paths.get(storagePath);
            Files.createDirectories(filePath);

            // 构建 biliurl
            String BV = Config.getMatcher("BV[^/]*$",url);
            String biliurl = BV != null ? "https://www.bilibili.com/video/" + BV : url;

            // 构建 yt-dlp 命令
            Process process = getProcess(filePath, snowId, biliurl);
            String jsonOutput = readOutput(process.getInputStream());
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException("下载失败，退出码: " + exitCode);
            }

            // 解析元数据
            ObjectMapper mapper = new ObjectMapper();
            JsonNode Rawdata = mapper.readTree(jsonOutput);
            saveMetadata(snowId, Rawdata, url);

            task.setStatus("COMPLETED");
        } catch (Exception e) {
            task.setStatus("FAILED");
            task.setErrorMessage(e.getMessage());
        } finally {
            taskRepository.save(task);
        }
    }

    private Process getProcess(Path filePath, Long snowId, String biliurl) throws IOException {
        String ext;
        ProcessBuilder pb = new ProcessBuilder(
                ytDlpPath,
                "-x",                                // 提取音频（关键参数）
                "--audio-format", "m4a",            // 强制转换为 M4a
                "--audio-quality", "0",             // 最高音质（0=最佳，320kbps）
                "--embed-thumbnail",                // 嵌入封面（如有）
                "--write-thumbnail",                // 单纯存储封面（如有）
                "--embed-metadata",                 // 嵌入标题、艺术家等元数据
                "-o", filePath.resolve(snowId + ".%(ext)s").toString(), // 输出路径模板
                "--print-json",  // 获取元数据
                biliurl
        );

        // 执行命令
        Process process = pb.start();
        return process;
    }

    // 解析 yt-dl 输出
    private String readOutput(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    // 保存元数据到 object_metadata 表
    private void saveMetadata(Long snowId, JsonNode Rawdata, String sourceUrl) {
        String filename = snowId + "." + Rawdata.get("ext").asText();
        Path filepath = Paths.get(storagePath, filename);
        String ThumbnailName = snowId + ".jpg";
        Path ThumbnailPath = Paths.get(storagePath, ThumbnailName);
        ObjectMetadata obj = new ObjectMetadata();
        obj.setId(snowId);
        obj.setSourceAddress(sourceUrl);
        obj.setContentType(Rawdata.get("ext").asText());
        obj.setFileName(Rawdata.get("title").asText() + "." + Rawdata.get("ext").asText());
        obj.setFilePath(filepath.toString());
        obj.setThumbnailPath(ThumbnailPath.toString());
        obj.setMusicLength(Config.SecondsToMinutes(Double.parseDouble(Rawdata.get("duration").asText())));
        obj.setArtist(Rawdata.get("uploader").asText());
        obj.setUploadTime(LocalDateTime.now());
        obj.setFileSize(new File(obj.getFilePath()).length());

        metadataRepository.save(obj);
    }

    public DownloadTask getTask(Long taskId) {
        return taskRepository.findById(taskId).orElseThrow();
    }
}
