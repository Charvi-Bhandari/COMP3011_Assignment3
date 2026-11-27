package comp3011;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SubtitleLoader {

    private final SubtitleClient client = new SubtitleClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // nanages background worker

    private Future<?> currentTask;
    private long generation = 0;

    public synchronized void load(File videoFile, SubtitleManager manager) {

        cancel();
        manager.clear();

        long requestGeneration = generation;

        // Load subtitles on a separate thread.
        currentTask = executor.submit(() -> loadSubtitles(videoFile, manager, requestGeneration));
    }

    private void loadSubtitles(File videoFile,
            SubtitleManager manager, long requestGeneration) {

        List<Subtitle> allSubtitles = new ArrayList<>();

        try {
            AudioExtractor.extractChunks(
                    videoFile.getAbsolutePath(),
                    (audioFile, offset) -> {

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException();
                }

                // Request subtitles for the current audio chunk
                List<Subtitle> subtitles = requestWithRetry(audioFile);

                // Adjust timestamps to match the original video
                for (Subtitle subtitle : subtitles) {
                    allSubtitles.add(new Subtitle(
                            subtitle.start() + offset,
                            subtitle.end() + offset,
                            subtitle.text()
                    ));
                }

                // Update subtitles after each chunk
                saveSubtitles(manager, allSubtitles,
                        requestGeneration);

                System.out.println("Chunk processed at: "
                        + offset + " seconds");
            });

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

        } catch (Exception e) {
            System.err.println("Could not load subtitles: "
                    + e.getMessage());
        }
    }
    
    private List<Subtitle> requestWithRetry(File audioFile) throws Exception {

        while (!Thread.currentThread().isInterrupted()) {
            try {
                return client.requestSubtitles(audioFile);
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                System.err.println("Subtitle request failed. Retrying in 5 seconds.");
                // Wait before retrying to avoid flooding the server.
                Thread.sleep(5000); // 5 Second delay in recieving stops
            }
        }

        throw new InterruptedException("Subtitle request cancelled");
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