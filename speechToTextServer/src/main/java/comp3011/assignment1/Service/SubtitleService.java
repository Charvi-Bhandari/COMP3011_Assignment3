package comp3011.assignment1.Service;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.multipart.MultipartFile;

public interface SubtitleService {
	
	// Transcribe audio and return text and time
	CompletableFuture<String> generateSubtitles(MultipartFile audio);

}
