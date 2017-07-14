package fun.albumtunes.changelisteners;

import javafx.scene.media.MediaPlayer;

public class OnMediaStopped implements Runnable{
	
	private MediaPlayer player;
	
	public OnMediaStopped(MediaPlayer player) {
		this.player = player;
	}
	
	@Override
	public void run() {
		if(player != null){
			this.player.dispose();
		}
		
	}
}
