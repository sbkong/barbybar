package com.au4a.barbybar.service;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import com.au4a.barbybar.entity.AudioFileEntity;
import com.au4a.barbybar.repository.AudioFileRepository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.UnsupportedAudioFileException;
import lombok.RequiredArgsConstructor;
import org.jaudiotagger.audio.AudioFile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BpmService {

    private final AudioFileRepository audioFileRepository;

    public int calculateBPM(String filePath) throws IOException, UnsupportedAudioFileException {
//        AudioDispatcher dispatcher = AudioDispatcherFactory.fromFile(new File(filePath), 1024, 512);
        File audioFile = new File(filePath);
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromPipe(audioFile.getAbsolutePath(),
            44100, 1024, 512);

        List<Double> onsetTimes = new ArrayList<>();

        OnsetHandler onsetHandler = (time, salience) -> onsetTimes.add(time);

        double sensitivity = 1.0; // 감도 조절
        double threshold = 8.0; // 임계값 조절
        PercussionOnsetDetector detector = new PercussionOnsetDetector(44100, 1024, onsetHandler,
            sensitivity, threshold);
        dispatcher.addAudioProcessor(detector);

        dispatcher.run();

        // 비트 간격을 계산하여 BPM 추정
        return estimateBPM(onsetTimes);
    }

    private int estimateBPM(List<Double> onsetTimes) {
        if (onsetTimes.size() < 2) {
            return 0; // 충분한 비트가 감지되지 않음
        }

        List<Double> intervals = new ArrayList<>();
        for (int i = 1; i < onsetTimes.size(); i++) {
            intervals.add(onsetTimes.get(i) - onsetTimes.get(i - 1));
        }

        double averageInterval = intervals.stream().mapToDouble(val -> val).average().orElse(0.0);
        if (averageInterval == 0.0) {
            return 0;
        }

        // BPM 계산: 60초를 평균 간격으로 나눔
        return (int) (60.0 / averageInterval);
    }

    public void saveAudioFile(AudioFile audioFile, int bpm) {

        AudioFileEntity insertAudio = AudioFileEntity.builder()
            .bpm(bpm)
            .filename(audioFile.getFile().getName())
            .filename(audioFile.getFile().getPath()).build();

        audioFileRepository.save(insertAudio);
    }
}
