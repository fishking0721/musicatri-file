package org.fishking0721.oss.pojo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
@Table(name = "thumbnail_metadata")
public class ThumbnailMetadata {

    @Id
    private Long id;

    @Column(name = "filename", nullable = false, length = 100)
    private String filename;  // 略缩图文件名

    @Column(name = "filepath", nullable = false, length = 500)
    private String filepath;  // 略缩图所在路径

    @Column(name = "filesize", nullable = false, length = 500)
    private Long filesize;  // 略缩图大小

    // 一个缩略图可以对应多个音频
    @OneToMany(mappedBy = "thumbnailMetadata", cascade = CascadeType.ALL)
    private List<AudioMetadata> audioMetadataList;

}
