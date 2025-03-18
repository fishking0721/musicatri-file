package org.example.oss.repository;

import org.example.oss.model.DownloadTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DownloadTaskRepository extends JpaRepository<DownloadTask, Long> {

}
