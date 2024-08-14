package com.au4a.barbybar.controller;

import com.au4a.barbybar.service.BpmService;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import lombok.RequiredArgsConstructor;
import org.jaudiotagger.audio.AudioFile;
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
        throws IOException, UnsupportedAudioFileException {
        String filePath = "path/to/save/" + file.getOriginalFilename();
        file.transferTo(new File(filePath));

        int bpm = bpmService.calculateBPM(filePath);
        AudioFile audioFile = new AudioFile();
        audioFile.setFile(file.getResource().getFile());
//        audioFile.
//            audioFile.setBpm(bpm);

        bpmService.saveAudioFile(audioFile);

        return new ResponseEntity<>(audioFile, HttpStatus.OK);
    }
}