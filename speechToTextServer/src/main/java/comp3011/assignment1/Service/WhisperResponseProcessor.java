package comp3011.assignment1.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class WhisperResponseProcessor {

    private final ObjectMapper objectMapper;

    public WhisperResponseProcessor(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Represent the subtitle response returned to the video player.
    public record Subtitle(double start, double end, String text) {
    }

    public String process(String result) {

        JsonNode response = objectMapper.readTree(result);

        List<Subtitle> subtitles = new ArrayList<>();

        for (JsonNode segment : response.path("segments")) {

            double noSpeechProb =
                    segment.path("no_speech_prob").asDouble();

            double aveLogProb =
                    segment.path("avg_logprob").asDouble();

            // Ignore segments only when both conditions are satisfied.
            if (noSpeechProb > 0.6 && aveLogProb < -1.0) {
                continue;
            }

            double start = segment.path("start").asDouble();
            double end = segment.path("end").asDouble();
            String text = segment.path("text").asText().trim();

            if (!text.isEmpty() && end > start) {
                subtitles.add(new Subtitle(start, end, text));
            }
        }

        return objectMapper.writeValueAsString(subtitles);
    }
}