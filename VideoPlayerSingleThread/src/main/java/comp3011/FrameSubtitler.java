package comp3011;

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
		// TODO Auto-generated method stub
		
	}

}
