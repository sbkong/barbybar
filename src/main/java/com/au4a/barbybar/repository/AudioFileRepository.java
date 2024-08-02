package com.au4a.barbybar.repository;

import com.au4a.barbybar.entity.AudioFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioFileRepository extends JpaRepository<AudioFileEntity, Long> {

}