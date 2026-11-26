package comp3011;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SubtitleLoader {

    private final SubtitleClient client = new SubtitleClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Future<?> currentTask;
    private long generation = 0;

    public synchronized void load(File videoFile, SubtitleManager manager) {

        cancel();
        manager.clear();

        long requestGeneration = generation;

        // Load subtitles on a separate thread.
        currentTask = executor.submit(() -> loadSubtitles(videoFile, manager, requestGeneration));
    }

    private void loadSubtitles(File videoFile, SubtitleManager manager, long requestGeneration) {

        File audioFile = null;

        try {
            System.out.println("Extracting audio...");
            audioFile = AudioExtractor.extractAudio(videoFile.getAbsolutePath());

            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            System.out.println("Requesting subtitles...");
            List<Subtitle> subtitles = client.requestSubtitles(audioFile);

            saveSubtitles(manager, subtitles, requestGeneration);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

        } catch (Exception e) {
            System.err.println("Could not load subtitles: " + e.getMessage());

        } finally {
            
            if (audioFile != null) {
                audioFile.delete(); // Delete the temporary audio file after use.
            }
        }
    }

    // Only save subtitles if the request is still current.
    private synchronized void saveSubtitles(SubtitleManager manager, List<Subtitle> subtitles, long requestGeneration) {

        if (requestGeneration == generation) {
            manager.setSubtitles(subtitles);
            System.out.println("Subtitles received: " + subtitles.size());
        }
    }

    public synchronized void cancel() {
        generation++;

        if (currentTask != null) {
            currentTask.cancel(true);
            currentTask = null;
        }
    }

    public synchronized void shutdown() {
        cancel();
        executor.shutdownNow();
    }
}