package comp3011;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.List;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

public class FrameSubtitler extends FrameProcessor  {
	 private final Java2DFrameConverter converter = new Java2DFrameConverter();

	    private volatile boolean enabled;

	    public FrameSubtitler(boolean enabled) {
	        this.enabled = enabled;
	       
	        subtitleManager.setSubtitles(List.of(
	            new Subtitle(0, 3, "This is the Ttemporaryy subtitle."), // Temporary subtitle data for testing playback synchronisation
	            new Subtitle(4, 7, "subtitlllleeeee"),
	            new Subtitle(8,12, "Simpsons Trailer..."),
	            new Subtitle(13, 17, "Testing, last subtitle..")
	        ));
	    }
	    
	    private final SubtitleManager subtitleManager =
	            new SubtitleManager();
	    
	    public SubtitleManager getSubtitleManager() {
	        return subtitleManager;
	    }

	    public boolean isEnabled() {
	        return enabled;
	    }

	    public void setEnabled(boolean enabled) {
	        this.enabled = enabled;
	    }

	@Override
	public void process(Frame frame, InfoFrame info) throws Exception {
		
		if (!enabled || frame.image == null) { 
            return;
        }		
		// Convert the frame timestamp from microseconds to seconds.
		double time = info.timestampUs() / 1_000_000.0;
		
		// Retrieve the subtitle matching the current frame timestamp.
		String text = subtitleManager.getSubtitle(time);
		
		// Skip image processing when there is no active subtitle.
		if (text.isEmpty()) {
		    return;
		}

        BufferedImage image = converter.convert(frame);

        if (image == null) {
            return;
        }

        Graphics2D graphics = image.createGraphics();

        try {
            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);


            int fontSize = Math.max(16, image.getHeight() / 22);

            Font font = new Font("SansSerif", Font.PLAIN, fontSize);
            graphics.setFont(font);

            int textWidth = graphics.getFontMetrics().stringWidth(text);

            int x = (image.getWidth() - textWidth) / 2;
            int y = image.getHeight() - fontSize;

            GlyphVector glyph = font.createGlyphVector(
                    graphics.getFontRenderContext(), text);

            Shape outline = glyph.getOutline(x, y);

            // Draw the black outline before filling the white text.
            graphics.setColor(Color.BLACK);
            graphics.setStroke(new BasicStroke(3));
            graphics.draw(outline);

            graphics.setColor(Color.WHITE);
            graphics.fill(outline);

        } finally {
            graphics.dispose();
        }

        Java2DFrameConverter.copy(image, frame);
		
	}

}
