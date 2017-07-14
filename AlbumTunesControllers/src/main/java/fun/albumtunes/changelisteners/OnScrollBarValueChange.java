package fun.albumtunes.changelisteners;

import fun.albumtunes.controllers.MediaFilemgmtController;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.*;
import javafx.scene.media.*;
import javafx.util.Duration;
import javafx.beans.value.ObservableValue;

public class OnScrollBarValueChange implements ChangeListener<Number> {

	private MediaPlayer currentPlayer;
	private Label currentSongTime;
	private Button playBackButton;

	public OnScrollBarValueChange(MediaPlayer currentPlayer, Label currentSongTime, Button playBackButton) {
		this.currentPlayer = currentPlayer;
		this.currentSongTime = currentSongTime;
		this.playBackButton = playBackButton;
	}

	@Override
	public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

		if (currentPlayer == null) {
			return; // currentPlayer has not been initalized, do nothing
		}

		double ratio = currentPlayer.getCurrentTime().toMinutes() / currentPlayer.getCycleDuration().toMinutes();

		// make sure the change is not from the AudioSpectrumListener update
		if (!String.format("%.5f", newValue.doubleValue()).equals(String.format("%.5f", ratio))) {

			// pause media player so it doesn't trigger an
			// event with the AudioSpectrumListener.. may not be necessary
			currentPlayer.pause();

			// get time relative to scroll bar in milliseconds
			double millis = currentPlayer.getCycleDuration().toMillis() * newValue.doubleValue();

			// convert to minutes and display above scroll bar
			String time = MediaFilemgmtController.convertDecimalMinutesToTimeMinutes(millis / 60000.0);
			currentSongTime.setText(time);

			// seek to the new found song location and play it
			Duration duration = new Duration(millis);
			currentPlayer.seek(duration);
			if (playBackButton.getText().equals("Pause")) { // if playback
															// button reads
															// "Pause" a song is
				currentPlayer.play(); // supposed to be playing so resume
										// playing
			} // otherwise leave it paused

		}

	}
}
