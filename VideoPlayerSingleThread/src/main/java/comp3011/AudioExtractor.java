package comp3011;

import java.io.File;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

public class AudioExtractor {

	private static final long CHUNK_DURATION = 30_000_000L;
    @FunctionalInterface
    public interface ChunkHandler {
        void handle(File audioFile, double offset) throws Exception;
    }
    public static void extractChunks(
            String videoPath,
            ChunkHandler handler) throws Exception {
        try (FFmpegFrameGrabber grabber =
                new FFmpegFrameGrabber(videoPath)) {

            grabber.start();
            if (grabber.getAudioChannels() == 0) {
                throw new IllegalStateException("Video has no audio.");
            }
            Frame frame = grabber.grabSamples();

            while (frame != null) {

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                long chunkStart = frame.timestamp;

                File audioFile = File.createTempFile(
                        "subtitle-chunk-", ".wav");

                try {
                    try (FFmpegFrameRecorder recorder =
                            new FFmpegFrameRecorder(
                                    audioFile.getAbsolutePath(), 1)) {

                        recorder.setFormat("wav");
                        recorder.setSampleRate(16000);
                        recorder.setAudioChannels(1);
                        recorder.start();

                        // Extract approximately 15 seconds of audio.
                        while (frame != null &&
                                frame.timestamp - chunkStart
                                < CHUNK_DURATION) {

                            if (Thread.currentThread().isInterrupted()) {
                                throw new InterruptedException();
                            }

                            recorder.recordSamples(
                                    frame.sampleRate,
                                    frame.audioChannels,
                                    frame.samples);

                            frame = grabber.grabSamples();
                        }

                        recorder.stop();
                    }

                  
                    handler.handle(
                            audioFile,
                            chunkStart / 1_000_000.0); // Send the chunk with its original timestamp.

                } finally {
                    audioFile.delete();
                }
            }

            grabber.stop();
        }
    }
}