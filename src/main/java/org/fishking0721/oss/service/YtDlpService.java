package org.fishking0721.oss.service;

import org.fishking0721.oss.config.YtDlpProperties;
import org.fishking0721.oss.pojo.dto.YtDlpAudioDownloadCommandDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 基于process命令行执行yt-dlp命令
 */
@Service
public class YtDlpService {

    @Autowired
    private YtDlpProperties ytDlpProperties;

    public Process executeAudioDownload(YtDlpAudioDownloadCommandDTO dto) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(ytDlpProperties.getLocation());
        command.add("-x");
        command.add("-o");
        command.add(dto.getOutputPath().toString());
        command.add("--audio-format");
        command.add(dto.getAudioFormat());
        command.add("--audio-quality");
        command.add(dto.getAudioQuality());
        command.add("--convert-thumbnails");
        command.add(dto.getConvertThumbnails());

        if (dto.isEmbedThumbnail()) command.add("--embed-thumbnail");
        if (dto.isWriteThumbnail()) command.add("--write-thumbnail");
        if (dto.isEmbedMetadata()) command.add("--embed-metadata");
        if (dto.isPrintJson()) command.add("--print-json");

        command.add(dto.getUrl());

        ProcessBuilder pb = new ProcessBuilder(command);
        return pb.start();
    }

}
