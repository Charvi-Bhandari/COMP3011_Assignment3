package comp3011;

import java.util.ArrayList;
import java.util.List;

public class SubtitleFormatter {

    private static final int maxLineLength = 42; // Subtitle text should not exceed 42 characters per line. (requirement)

    public static List<String> wrapText(String text) {

        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        String[] words = text.trim().split("\\s+");

        for (String word : words) {

            // Handle words that exceed the maximum line length.
            while (word.length() > maxLineLength) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine.setLength(0);
                }

                lines.add(word.substring(0, maxLineLength));
                word = word.substring(maxLineLength);
            }

            int space = currentLine.length() == 0 ? 0 : 1;

            // Start a new line if the next word will not fit.
            if (currentLine.length() + space + word.length()
                    > maxLineLength) {

                lines.add(currentLine.toString());
                currentLine.setLength(0);
            }

            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }

            currentLine.append(word);
        }

        // Store the remaining text.
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }
}