package comp3011.assignment1.Controllers;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import comp3011.assignment1.Service.SubtitleService;

@RestController
public class SubtitleController {

    private final SubtitleService subtitleService;

    public SubtitleController(SubtitleService subtitleService) {
        this.subtitleService = subtitleService;
    }

    // Receive audio from the video player and return timed subtitles.
    @PostMapping(value = "/api/v1/subtitles",
                 produces = "application/json")
    public CompletableFuture<String> generateSubtitles(
            @RequestParam("audio") MultipartFile audio) {

        return subtitleService.generateSubtitles(audio);
    }
}