package fun.albumtunes.changelisteners;

import fun.albumtunes.utilities.MEDIA_PLAYER_STATE;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public abstract class MediaPlayerStateChanged implements ChangeListener<MEDIA_PLAYER_STATE>{

	@Override
	public abstract void changed(ObservableValue<? extends MEDIA_PLAYER_STATE> observable, MEDIA_PLAYER_STATE oldValue,
			MEDIA_PLAYER_STATE newValue);

}
