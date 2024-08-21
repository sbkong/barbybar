package com.au4a.barbybar.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import org.springframework.stereotype.Service;

@Service
public class AudioFileConvertService {
    public static void convertMp3ToWav(File mp3File, File wavFile)
        throws IOException, UnsupportedAudioFileException, LineUnavailableException, JavaLayerException {
        try (FileInputStream fileInputStream = new FileInputStream(mp3File);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            AudioInputStream audioInputStream = decodeMp3(bufferedInputStream)) {

            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
        }
    }

    private static AudioInputStream decodeMp3(InputStream mp3InputStream)
        throws JavaLayerException, UnsupportedAudioFileException, IOException {
        Bitstream bitstream = new Bitstream(mp3InputStream);
        SampleBuffer output = null;

        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (AdvancedPlayer player = new AdvancedPlayer(mp3InputStream)) {
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent evt) {
                    // MP3 디코딩 종료 후 처리
                }
            });

            player.play();
            while ((output = (SampleBuffer) player.decodeFrame()) != null) {
                byteArrayOutputStream.write(output.getBuffer(), 0, output.getBufferLength());
            }

            byte[] audioData = byteArrayOutputStream.toByteArray();
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            return new AudioInputStream(bais, format, audioData.length / format.getFrameSize());
        }
    }
}
