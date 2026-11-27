package comp3011.assignment1.Service;

import java.util.concurrent.atomic.DoubleAdder;
import org.springframework.stereotype.Service;

@Service
public class SubtitleUsageService {
    private final DoubleAdder totalUsage = new DoubleAdder(); // Thread-safe storage for cumulative transcription duration.

    public void addUsage(double seconds) {
        totalUsage.add(seconds);
    }

    public double getUsage() {
        return totalUsage.sum();
    }
}