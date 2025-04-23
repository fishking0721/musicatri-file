package org.fishking0721.oss.config;

import org.apache.commons.io.FilenameUtils;
import org.fishking0721.oss.exception.StorageException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class FileSecurity {
    // 允许的MIME类型白名单
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "audio/mpeg", "video/mp4"
    );

    // 允许的文件扩展名白名单
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "mp3", "mp4", "wav"
    );

    public void validateFile(MultipartFile file) throws IOException {
        // 验证MIME类型
        String mimeType = file.getContentType();
        if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
            throw new StorageException("type not allowed");
        }

        // 验证文件扩展名
        String originalName = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new StorageException("Invalid file extension");
        }

        // 验证文件头byte（魔数）
        try (InputStream is = file.getInputStream()){
            byte[] header = new byte[8];
            is.read(header);
            if (!isJpeg(header) && !isPng(header) && !isMp3(header)) {
                throw new StorageException("File content does not match type");
            }
        }
    }
    // 生成安全存储路径
    public Path SafePath(String originalName) {
        // 移除路径遍历字符
        String safeName = originalName.replaceAll("\\.\\.", "");
        // 替换特殊字符
        safeName = safeName.replaceAll("[^a-zA-Z0-9.-]", "_");
        return Paths.get(safeName);
    }

    private boolean isMp3(byte[] header) {
        return header[0] == 'I' && header[1] == 'D' && header[2] == '3';
    }

    private boolean isPng(byte[] header) {
        return header[0] == (byte)0x89 && header[1] == 'P' && header[2] == 'N'
                && header[3] == 'G' && header[4] == (byte)0x0D && header[5] == (byte)0x0A
                && header[6] == (byte)0x1A && header[7] == (byte)0x0A;
    }

    private boolean isJpeg(byte[] header) {
        return header[0] == (byte)0xFF && header[1] == (byte)0xD8;
    }
}
