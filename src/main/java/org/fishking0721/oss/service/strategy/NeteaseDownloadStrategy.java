package org.fishking0721.oss.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import org.fishking0721.oss.client.NeteaseApi;
import org.fishking0721.oss.config.Config;
import org.fishking0721.oss.pojo.dto.AudioDownloadTaskCreateDTO;
import org.fishking0721.oss.pojo.dto.AudioMetadataDTO;
import org.fishking0721.oss.pojo.dto.ThumbnailMetadataDTO;
import org.fishking0721.oss.service.AudioMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Component
public class NeteaseDownloadStrategy extends DownloadStrategy {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AudioMetadataService audioMetadataService;

    @Autowired
    private NeteaseApi neteaseApi;

    @Autowired
    private Config config;

    @Value("${storage.location}")
    private String storagePath;  // 本地仓库路径

    @Override
    public void execute(AudioDownloadTaskCreateDTO dto) throws Exception {
        Long taskId = dto.getId();
        String songId = config.getMatcher("id=([^&]+)", dto.getUrl());  // 使用正则获取songId

        JsonNode urlResponse = neteaseApi.getSongUrl(songId, "higher");
        String audioDownloadUrl = parseDownloadUrl(urlResponse);  // 音频下载链接

        String audioFilename = taskId + "." + dto.getAudioFormat();  // 文件名
        Path audioFilepath = Paths.get(storagePath, audioFilename);

        downloadResource(audioDownloadUrl, audioFilepath);  // 下载音频

        JsonNode songDetails = neteaseApi.getSongDetail(songId);
        String thumbnailDownloadUrl = songDetails.findPath("picUrl").asText();  // 略缩图下载链接

        String ThumbnailFilename = taskId + "." + dto.getThumbnailFormat();  // 封面名
        Path ThumbnailFilepath = Paths.get(storagePath, ThumbnailFilename);

        downloadResource(thumbnailDownloadUrl, ThumbnailFilepath);  // 下载封面略缩图

//        JsonNode SongCover = neteaseApi.getSongDynamicCover(songid);

        // 下载完成，将数据插入数据库
        AudioMetadataDTO audioMetadataDTO = AudioMetadataDTO.builder()
                .id(taskId)
                .filename(audioFilename)
                .filepath(audioFilepath.toString())
                .filesize(Files.size(audioFilepath))
                .duration(String.valueOf((songDetails.path("songs").get(0).path("dt")).asDouble() / 1000))
                .artist(songDetails.findPath("ar").get(0).path("name").asText())
                .uploaderId(dto.getUploaderId())
                .sourceUrl(dto.getUrl())
                .contentType(dto.getAudioFormat())
                .uploadTime(LocalDateTime.now())
                .build();

        ThumbnailMetadataDTO thumbnailMetadataDTO = ThumbnailMetadataDTO.builder()
                .id(taskId)
                .filename(ThumbnailFilename)
                .filepath(ThumbnailFilepath.toString())
                .filesize(Files.size(ThumbnailFilepath))
                .build();

        audioMetadataService.saveAudioMetadata(audioMetadataDTO, thumbnailMetadataDTO);  // 保存元数据
    }

    private void downloadResource(String url, Path path) throws IOException {
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
        byte[] bytes = response.getBody();

        if (bytes == null) {
            throw new RuntimeException("Audio download failed, file data is null");
        }

        try {
            Files.write(path, bytes);  // 保存歌曲文件到本地
        } catch (Exception e) {
            Files.deleteIfExists(path);
            throw e;  // 抛出异常时删除已经写入的文件
        }
    }

    private String parseDownloadUrl(JsonNode urlResponse) {
        if (urlResponse.has("data") && urlResponse.get("data").isArray()) {
            JsonNode firstData = urlResponse.get("data").get(0);
            if (firstData.has("url")) {
                return firstData.get("url").asText();
            }
        }
        throw new RuntimeException("unable to find download url");
    }

    @Override
    public String getType() {
        return "163:audio";
    }
}
