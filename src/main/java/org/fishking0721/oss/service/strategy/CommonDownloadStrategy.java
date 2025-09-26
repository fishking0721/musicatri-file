package org.fishking0721.oss.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fishking0721.oss.config.Config;
import org.fishking0721.oss.config.StorageProperties;
import org.fishking0721.oss.pojo.dto.AudioDownloadTaskCreateDTO;
import org.fishking0721.oss.pojo.dto.AudioMetadataDTO;
import org.fishking0721.oss.pojo.dto.ThumbnailMetadataDTO;
import org.fishking0721.oss.pojo.dto.YtDlpAudioDownloadCommandDTO;
import org.fishking0721.oss.service.AudioMetadataService;
import org.fishking0721.oss.service.YtDlpService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public abstract class CommonDownloadStrategy extends DownloadStrategy {

    @Autowired
    private AudioMetadataService audioMetadataService;

    @Autowired
    private YtDlpService ytDlpService;

    @Autowired
    private StorageProperties storageProperties;

    private static final Config config = new Config();

    @Override
    public void execute(AudioDownloadTaskCreateDTO dto) throws Exception {
        Long taskId = dto.getId();
        String url = dto.getUrl();

        String storagePath = storageProperties.getLocation();  // 构建目标路径
        Path outputAudioPath = Paths.get(storagePath).resolve(dto.getId() + ".%(ext)s");

        YtDlpAudioDownloadCommandDTO commandDTO = YtDlpAudioDownloadCommandDTO.builder()
                .audioFormat(dto.getAudioFormat())  // 目标音频格式
                .convertThumbnails(dto.getThumbnailFormat())  // 目标封面格式
                .outputPath(outputAudioPath)
                .printJson(true)
                .url(url)
                .build();

        Process downloadProcess = ytDlpService.executeAudioDownload(commandDTO);  // 使用yt-dlp下载
        JsonNode songDetails = handleYtDlpOutputJson(downloadProcess.getInputStream());

        int exitCode = downloadProcess.waitFor();  // 阻塞等待下载完成
        if (exitCode != 0) {
            throw new IOException("download failed with exit code " + exitCode);
        }

        // String audioFilename = snowId + "." + Rawdata.get("ext").asText();

        String audioFilename = taskId + "." + dto.getAudioFormat();  // 音频文件名
        Path audioFilepath = Paths.get(storagePath, audioFilename);  // 音频文件路径

        String thumbnailFilename = taskId + "." + dto.getThumbnailFormat();  // 略缩图文件名
        Path thumbnailFilepath = Paths.get(storagePath, thumbnailFilename);  // 略缩图路径

        // 计算音乐长度
        long durationMs = -1;
        if (songDetails.get("duration") != null) {
            durationMs = (long) (Double.parseDouble(songDetails.get("duration").asText()) * 1000);
        }
        //解决nullpointer异常,获取不到时，将artist设置为error
        String songDetailsArtist;
        if (songDetails.get("uploader") == null) {
            songDetailsArtist = config.NodetoString(songDetails.get("album_artists"));
        } else {
            songDetailsArtist = songDetails.get("uploader").asText();
        }

        // 下载完成，向数据库插入数据
        AudioMetadataDTO audioMetadataDTO = AudioMetadataDTO.builder()
                .id(taskId)
                .filename(audioFilename)
                .filepath(audioFilepath.toString())
                .filesize(Files.size(audioFilepath))
                .duration(String.valueOf(durationMs))
                .artist(songDetailsArtist)
                .uploaderId(dto.getUploaderId())
                .sourceUrl(dto.getUrl())
                .contentType(dto.getAudioFormat())
                .uploadTime(LocalDateTime.now())
                .build();

        ThumbnailMetadataDTO thumbnailMetadataDTO = ThumbnailMetadataDTO.builder()
                .id(taskId)
                .filename(thumbnailFilename)
                .filepath(thumbnailFilepath.toString())
                .filesize(Files.size(thumbnailFilepath))
                .build();

        audioMetadataService.saveAudioMetadata(audioMetadataDTO, thumbnailMetadataDTO);  // 保存元数据
    }

    // 解析yt-dl输出
    private JsonNode handleYtDlpOutputJson(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(sb.toString());
    }
}
