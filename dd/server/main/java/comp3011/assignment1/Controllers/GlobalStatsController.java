package comp3011.assignment1.Controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import comp3011.assignment1.Service.GlobalStatsService;
import comp3011.assignment1.Service.SubtitleUsageService;
import comp3011.assignment1.response.GlobalStatsResponse;

@RestController
@RequestMapping("/api/v1/global")
public class GlobalStatsController {
	private final GlobalStatsService globalStatsService;
	private final SubtitleUsageService subtitleUsageService;

    public GlobalStatsController(GlobalStatsService globalStatsService,
    		SubtitleUsageService subtitleUsageService) {
        this.globalStatsService = globalStatsService;
        this.subtitleUsageService = subtitleUsageService;
    }

    @GetMapping("/stats")
    public GlobalStatsResponse getGlobalStats() {

        return new GlobalStatsResponse(
                globalStatsService.getInputTokens(),
                globalStatsService.getOutputTokens()
        );
    }
    
 // Return cumulative transcription usage in seconds.
    @GetMapping("/usage")
    public Map<String, Double> getUsage() {
        return Map.of("usage", subtitleUsageService.getUsage());
    }
}