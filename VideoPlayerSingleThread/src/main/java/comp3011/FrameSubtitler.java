package comp3011;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

public class FrameSubtitler extends FrameProcessor  {
	 private final Java2DFrameConverter converter = new Java2DFrameConverter();

	    private volatile boolean enabled;

	    public FrameSubtitler(boolean enabled) {
	        this.enabled = enabled;
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

        BufferedImage image = converter.convert(frame);

        if (image == null) {
            return;
        }

        Graphics2D graphics = image.createGraphics();

        try {
            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            String text = "This is a test subtitle.";

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
