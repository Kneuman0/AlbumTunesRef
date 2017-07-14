package biz.albumtunes.textinput;

import fun.personalUse.dataModel.PlaylistBean;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;

public class ChangePlaylistAlert extends TextInputAlert{
	
	private TextField playlistName;
	
	public ChangePlaylistAlert(PlaylistBean playlist){
		
		super.setHeaderText("Please enter a new name for your playlist");
		super.setTitle("Change Playlist Name");
		super.setContentText(null);
		
		grid.setPadding(new Insets(20, 150, 0, 10));
		
		TextField playlistName = new TextField();
		playlistName.setText(playlist.getName());

		grid.add(playlistName, 0, 0);
	}
	
	public String getPlaylistName(){
		return playlistName.getText();
	}

}
