package com.ttogal.domain.file.repository;

import com.ttogal.domain.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File,Long> {

}
