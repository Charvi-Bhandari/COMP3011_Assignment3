package comp3011;

import java.util.List;

public class SubtitleManager {
	// immutable list helps safely updating subtitles, being read and updated.
    private volatile List<Subtitle> subtitles = List.of();

    public void setSubtitles(List<Subtitle> subtitles) {
        this.subtitles = List.copyOf(subtitles);
    }

    // Find the subtitle matching the current playback position.
    public String getSubtitle(double time) {

        for (Subtitle subtitle : subtitles) {
            if (subtitle.isActive(time)) {
                return subtitle.text();
            }
        }

        return "";
    }

    // Remove subtitles when they are no longer required.
    public void clear() {
        subtitles = List.of();
    }
}
