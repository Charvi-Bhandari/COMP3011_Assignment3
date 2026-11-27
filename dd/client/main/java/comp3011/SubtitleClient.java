package comp3011;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class SubtitleClient {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String SERVER_URL =
            "http://localhost:8080/api/v1/subtitles";

    public List<Subtitle> requestSubtitles(File audioFile)
            throws Exception {
      
        String boundary = UUID.randomUUID().toString();  // Create a unique boundary for the multipart upload.

        String header = "--" + boundary + "\r\n"
                + "Content-Disposition: form-data; "
                + "name=\"audio\"; filename=\"audio.wav\"\r\n"
                + "Content-Type: audio/wav\r\n\r\n";

        String footer = "\r\n--" + boundary + "--\r\n";

       
        HttpRequest.BodyPublisher body =
                HttpRequest.BodyPublishers.concat(
                    HttpRequest.BodyPublishers.ofString(header), 
                    HttpRequest.BodyPublishers.ofFile(audioFile.toPath()),  // Combine the multipart headers and original audio file.
                    HttpRequest.BodyPublishers.ofString(footer)
                );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL))
                .timeout(Duration.ofMinutes(4))
                .header("Content-Type",
                        "multipart/form-data; boundary=" + boundary)
                .POST(body)
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) { // Do not attempt to parse unsuccessful server responses.
            throw new IllegalStateException(
                    "Subtitle server returned " + response.statusCode());
        }
      
        return mapper.readValue(
                response.body(),
                new TypeReference<List<Subtitle>>() {});  // Convert the JSON response into timed subtitle records.
    }
}