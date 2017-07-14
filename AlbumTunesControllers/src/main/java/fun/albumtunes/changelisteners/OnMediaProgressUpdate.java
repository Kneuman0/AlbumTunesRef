package fun.albumtunes.changelisteners;

import fun.albumtunes.utilities.MediaUtils;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.media.MediaPlayer;

public class OnMediaProgressUpdate implements javafx.scene.media.AudioSpectrumListener {
	
	private Label songTime;
	private ScrollBar scrollBar;
	private MediaPlayer currentPlayer;
	
	public OnMediaProgressUpdate(MediaPlayer currentPlayer, ScrollBar scrollBar, Label songTime) {
		this.scrollBar = scrollBar;
		this.currentPlayer = currentPlayer;
		this.songTime = songTime;
	}
	
	@Override
	public void spectrumDataUpdate(double timestamp, double duration,
			float[] magnitudes, float[] phases) {
		double scrollBarValue = currentPlayer.getCurrentTime().toSeconds()/
				currentPlayer.getTotalDuration().toSeconds();
		scrollBar.setValue(scrollBarValue);		
		songTime.setText(MediaUtils.convertDecimalMinutesToTimeMinutes(
				(currentPlayer.getCurrentTime().toMinutes())));
	}
}
