package comp3011;

// Subtitle and the time interval during which it should appear in the video.
public record Subtitle(
        double start,
        double end,
        String text) {

    // Check whether this subtitle should appear at the given time.
    public boolean isActive(double time) {
        return time >= start && time < end;
    }
}