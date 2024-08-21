package com.au4a.barbybar.controller;

import com.au4a.barbybar.service.BpmService;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * The type Bpm controller.
 */
@RestController
@RequestMapping("/audio")
@RequiredArgsConstructor
@Slf4j
public class BpmController {

    final private BpmService bpmService;

    /**
     * Upload audio response entity.
     *
     * @param file the file
     * @return the response entity
     * @throws IOException the io exception
     * @throws UnsupportedAudioFileException the unsupported audio file exception
     */
    @PostMapping("/upload")
    public ResponseEntity<AudioFile> uploadAudio(@RequestParam("file") MultipartFile file)
        throws IOException, UnsupportedAudioFileException, CannotReadException, TagException, InvalidAudioFrameException, ReadOnlyFileException {
// 파일을 저장할 디렉토리와 경로 설정
        String uploadDir = "C:/uploads/";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs(); // 디렉토리가 없으면 생성
        }

        // 저장할 파일 경로 설정
        String filePath = uploadDir + file.getOriginalFilename();
        File savedFile = new File(filePath);

        // 파일 저장
        file.transferTo(savedFile);

        // BPM 계산
        int bpm = bpmService.calculateBPM(filePath);

        // JAudioTagger의 AudioFile을 사용하여 파일 처리
        AudioFile audioFile = AudioFileIO.read(savedFile);

        // 추가 정보 저장 및 응답 반환
        bpmService.saveAudioFile(audioFile, bpm);

        log.debug("bpm={}", bpm);
        return new ResponseEntity<>(audioFile, HttpStatus.OK);
    }
}