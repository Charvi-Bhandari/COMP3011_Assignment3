package comp3011;

import java.io.File;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

public class AudioExtractor {

    public static File extractAudio(String videoPath) throws Exception {

       
        File audioFile = File.createTempFile("subtitle-audio-", ".wav");  // Create a temporary WAV file for transcription.
        try (FFmpegFrameGrabber grabber =
                new FFmpegFrameGrabber(videoPath)) {

            grabber.start();

            if (grabber.getAudioChannels() == 0) {
                throw new IllegalStateException("Video has no audio."); // Check whether the video contains an audio track.
            }

            try (FFmpegFrameRecorder recorder =
                    new FFmpegFrameRecorder(
                            audioFile.getAbsolutePath(), 1)) {
             
                recorder.setFormat("wav"); 
                recorder.setSampleRate(16000);
                recorder.setAudioChannels(1); // Use mono audio to reduce the upload file size.

                recorder.start();

                Frame frame;

               
                while ((frame = grabber.grabSamples()) != null) {

                    recorder.recordSamples(
                            frame.sampleRate,  // Extract audio samples without processing video frames.
                            frame.audioChannels,
                            frame.samples);
                }

                recorder.stop();
            }

            grabber.stop();

            return audioFile;

        } catch (Exception e) {
           
            audioFile.delete(); // Remove incomplete audio files if extraction fails.
            throw e;
        }
    }
}