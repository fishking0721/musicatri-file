package org.fishking0721.oss.repository;
import org.fishking0721.oss.pojo.model.ObjectMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ObjectMetadataRepositoryTest {
    @Autowired
    private ObjectMetadataRepository repository;

    @Test
    void testCRUDOperations() {
        // 创建测试数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setFileName("test.txt");
        metadata.setFilePath("/storage/test.txt");
        metadata.setUploadTime(LocalDateTime.now());

        // 测试保存
        ObjectMetadata saved = repository.save(metadata);
        assertNotNull(saved.getId());

        // 测试查询
        Optional<ObjectMetadata> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());

        // 测试删除
        repository.deleteById(saved.getId());
        assertEquals(0, repository.count());
    }
}
