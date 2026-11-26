package comp3011.assignment1.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import tools.jackson.databind.ObjectMapper;

@Service
@Profile("!local")
public class WhisperSubtitleService implements SubtitleService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final SubtitleUsageService usageService;
    private final WhisperResponseProcessor responseProcessor;

    @Value("${openai.api.key}")
    private String apiKey;

    public WhisperSubtitleService(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            SubtitleUsageService usageService,
            WhisperResponseProcessor responseProcessor) {

        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com")
                .build();

        this.objectMapper = objectMapper;
        this.usageService = usageService;
        this.responseProcessor = responseProcessor;
    }

    @Override
    public CompletableFuture<String> generateSubtitles(
            MultipartFile audio) {

             return CompletableFuture.supplyAsync(() -> { // Read uploaded audio before sending the cloud request.

            try {
                return audio.getBytes();
            } catch (Exception e) {
                throw new RuntimeException("Could not read audio.", e);
            }

        }).thenCompose(audioBytes -> {

            MultipartBodyBuilder bodyBuilder =
                    new MultipartBodyBuilder();

            ByteArrayResource audioResource =
                    new ByteArrayResource(audioBytes) {

                @Override
                public String getFilename() {
                    return "recording.wav";
                }
            };

            // Whisper supports word and segment level timestamps.
            bodyBuilder.part("model", "whisper-1");
            bodyBuilder.part("response_format", "verbose_json");

            bodyBuilder.part(
                    "timestamp_granularities[]", "word");

            bodyBuilder.part(
                    "timestamp_granularities[]", "segment");

            bodyBuilder.part("file", audioResource)
                    .contentType(
                            MediaType.parseMediaType("audio/wav"));

            // Send the request asynchronously using the existing WebClient.
            return webClient.post()
                    .uri("/v1/audio/transcriptions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(
                            bodyBuilder.build()))
                    .retrieve()							// Retrieve HTTP Response
                    .bodyToMono(String.class)
                    .timeout(Duration.ofMinutes(3))		// Prevents indefinite wait from cloud service
                    .map(this::processResponse)
                    .toFuture();						// Converts the result into a CompletableFuture
        });
    }
    

    private String processResponse(String result) {

        try {
            // Record the duration of the successfully transcribed audio.
            double duration = objectMapper.readTree(result)
                    .path("duration").asDouble(0.0);

            usageService.addUsage(duration);

            return responseProcessor.process(result);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Could not process transcription response.", e);
        }
    }
}