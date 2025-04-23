package org.example.oss.repository;

import org.example.oss.pojo.model.DownloadTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DownloadTaskRepository extends JpaRepository<DownloadTask, Long> {

}
