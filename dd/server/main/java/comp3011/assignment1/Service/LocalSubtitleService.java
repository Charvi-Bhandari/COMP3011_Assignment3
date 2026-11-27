package comp3011.assignment1.Service;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Profile("local")
public class LocalSubtitleService implements SubtitleService {

    @Override
    public CompletableFuture<String> generateSubtitles(MultipartFile audio) {

        // Simulate the server response - Stub to test without credits
        return CompletableFuture.completedFuture("""
                [
                    {
                        "start": 0.0,
                        "end": 3.0,
                        "text": "Testing subtitles from the server."
                    },
                    {
                        "start": 4.0,
                        "end": 7.0,
                        "text": "Second time frame test, just seeing if this works! Simpsons Movie Ttrailer!"
                    }
                ]
                """);
    }
}