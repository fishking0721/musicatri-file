package org.fishking0721.oss.service;

import cn.hutool.core.lang.Snowflake;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.fishking0721.oss.client.NeteaseApi;
import org.fishking0721.oss.config.Config;
import org.fishking0721.oss.pojo.model.DownloadTask;
import org.fishking0721.oss.pojo.model.ObjectMetadata;
import org.fishking0721.oss.repository.DownloadTaskRepository;
import org.fishking0721.oss.repository.ObjectMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Objects;
@Service
public class AsyncTaskService {
    @Value("${storage.location}")
    private String storagePath;
    @Value("${yt-dlp.location}")
    private String ytDlpPath;

    @Autowired
    private DownloadTaskRepository taskRepository;

    @Autowired
    private ObjectMetadataRepository metadataRepository;

    @Autowired
    private NeteaseApi neteaseApi;

    private Config config = new Config();

    // 异步下载核心逻辑
    @Async
    public void downloadVideoAsync(Long taskId, String url) {
        DownloadTask task = taskRepository.findById(taskId).orElseThrow();
        task.setStatus("DOWNLOADING");
        taskRepository.save(task);

        try {
            Path filePath = Paths.get(storagePath);
            Files.createDirectories(filePath);

            // 构建 biliurl
            String BV = config.getMatcher("/(BV[^/]+)",url);
            String urlbuild = !Objects.equals(BV, "") ? "https://www.bilibili.com/video/" + BV : url;

            // 构建 yt-dlp 命令
            Process process = getProcess(filePath, taskId, urlbuild);
            String jsonOutput = readOutput(process.getInputStream());
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new IOException("下载失败，退出码: " + exitCode);
            }

            // 解析元数据
            ObjectMapper mapper = new ObjectMapper();
            JsonNode Rawdata = mapper.readTree(jsonOutput);
            saveMetadata(taskId, Rawdata, url);

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
                "--convert-thumbnails", "jpg", // 强制将缩略图转为 JPG（核心参数）
                "--embed-metadata",                 // 嵌入标题、艺术家等元数据
                "-o", filePath.resolve(snowId + ".%(ext)s").toString(), // 输出路径模板
//                "-o", filePath.resolve(snowId + ".m4a").toString(), // 输出路径模板
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
//        String filename = snowId + "." + Rawdata.get("ext").asText();
        String filename = snowId + "." + "m4a";
        Path filepath = Paths.get(storagePath, filename);
        String ThumbnailName = snowId + ".jpg";
        Path ThumbnailPath = Paths.get(storagePath, ThumbnailName);
        ObjectMetadata obj = new ObjectMetadata();
        obj.setId(snowId);
        obj.setSourceAddress(sourceUrl);
//        obj.setContentType(Rawdata.get("ext").asText());
        obj.setContentType("m4a");
//        obj.setFileName(Rawdata.get("title").asText() + "." + Rawdata.get("ext").asText());
        obj.setFileName(Rawdata.get("title").asText() + "." + "m4a");
        obj.setFilePath(filepath.toString());
        obj.setThumbnailPath(ThumbnailPath.toString());
        obj.setMusicLength(Rawdata.get("duration").asText());
        obj.setArtist(Rawdata.get("uploader").asText());
        obj.setUploadTime(LocalDateTime.now());
        obj.setFileSize(new File(obj.getFilePath()).length());

        metadataRepository.save(obj);
    }


    @Async
    public void downloadNeteasemusic(Long taskId, String songid) {
        DownloadTask task = taskRepository.findById(taskId).orElseThrow();
        task.setStatus("DOWNLOADING");
        taskRepository.save(task);

        // 获取歌曲下载URL
        JsonNode urlResponse = neteaseApi.getSongUrl(songid, "higher");
        String downloadUrl = parseDownloadUrl(urlResponse);
        // 获取歌曲详情
        JsonNode SongDetail = neteaseApi.getSongDetail(songid);
        String downloadUrlpic = SongDetail.findPath("picUrl").asText();
//        JsonNode SongCover = neteaseApi.getSongDynamicCover(songid);
        // 下载歌曲文件
        byte[] fileBytes;
        try {
            Path filePath = Paths.get(storagePath);
            Files.createDirectories(filePath);

            // 通过RestTemplate下载歌曲文件
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> response = restTemplate.getForEntity(downloadUrl, byte[].class);
            fileBytes = response.getBody();
            // 保存歌曲文件到本地
            Files.write(filePath.resolve(taskId + ".m4a"), fileBytes);
            // 下载歌曲封面
            RestTemplate restTemplatepic = new RestTemplate();
            ResponseEntity<byte[]> responsepic = restTemplatepic.getForEntity(downloadUrlpic, byte[].class);
            fileBytes = responsepic.getBody();
            Files.write(filePath.resolve(taskId + ".jpg"), fileBytes);

            // 保存元数据
            saveMetadata163(taskId, SongDetail, songid);

            task.setStatus("COMPLETED");
        } catch (Exception e) {
            task.setStatus("FAILED");
            task.setErrorMessage(e.getMessage());
            throw new RuntimeException("歌曲下载失败", e);
        } finally {
            taskRepository.save(task);
        }
    }

    private String parseDownloadUrl(JsonNode urlResponse) {
        if (urlResponse.has("data") && urlResponse.get("data").isArray()) {
            JsonNode firstData = urlResponse.get("data").get(0);
            if (firstData.has("url")) {
                return firstData.get("url").asText();
            }
        }
        return null;
    }


    // 网易保存元数据
    private void saveMetadata163(Long snowId, JsonNode Rawdata, String sourceUrl) {
        String filename = snowId + ".m4a";
        Path filepath = Paths.get(storagePath, filename);
        String ThumbnailName = snowId + ".jpg";
        Path ThumbnailPath = Paths.get(storagePath, ThumbnailName);
        ObjectMetadata obj = new ObjectMetadata();
        obj.setId(snowId);
        obj.setSourceAddress(sourceUrl);
        obj.setContentType("m4a");
        obj.setFileName(Rawdata.path("songs").get(0).path("name").asText());
        obj.setFilePath(filepath.toString());
        obj.setThumbnailPath(ThumbnailPath.toString());
        obj.setMusicLength(String.valueOf((Rawdata.path("songs").get(0).path("dt")).asDouble()/1000));
        obj.setArtist(Rawdata.findPath("ar").get(0).path("name").asText());
        obj.setUploadTime(LocalDateTime.now());
        obj.setFileSize(new File(obj.getFilePath()).length());

        metadataRepository.save(obj);
    }

}
