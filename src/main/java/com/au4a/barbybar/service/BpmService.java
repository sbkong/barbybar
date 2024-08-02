package com.au4a.barbybar.service;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import com.au4a.barbybar.repository.AudioFileRepository;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import lombok.RequiredArgsConstructor;
import org.jaudiotagger.audio.AudioFile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BpmService {

    private final AudioFileRepository audioFileRepository;

    public int calculateBPM(String filePath) throws IOException, UnsupportedAudioFileException {
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(new File(filePath), 1024, 512);
//        BeatRootOnsetDetector beatDetector = new BeatRootOnsetDetector(1024, 44100);
//        dispatcher.addAudioProcessor(beatDetector);
//        dispatcher.run();
//        return beatDetector.getBPM();
        return 100;
    }

    public void saveAudioFile(AudioFile audioFile) {
//        audioFileRepository.save(audioFile);
    }
}
