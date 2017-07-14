package biz.albumtunes.textinput;

import fun.personalUse.dataModel.FileBean;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class ChangeMetadataAlert extends TextInputAlert{
	private TextField songName, artist, album;

	public ChangeMetadataAlert(FileBean song) {
		
		 super.setHeaderText("Change song metadata");
		 super.setTitle("Change Song Metadata");
		 super.setContentText(null);

		 this.songName = new TextField();
		 songName.setText(song.getSongName());
		 songName.setPrefWidth(300);
		 this.artist = new TextField();
		 artist.setText(song.getArtist());
		 this.album = new TextField();
		 album.setText(song.getAlbum());

		 grid.add(new Label("Song Name:"), 0, 0);
		 grid.add(songName, 0, 1);
		 grid.add(new Label("Artist:"), 0, 2);
		 grid.add(artist, 0, 3);
		 grid.add(new Label("Album:"), 0, 4);
		 grid.add(album, 0, 5);
		 
		 // add text fields to Alert box
		 super.getDialogPane().setContent(grid);
	}
	
	public String getSongName(){
		return songName.getText();
	}
	
	public String getArtist(){
		return artist.getText();
	}
	
	public String getAlbum(){
		return album.getText();
	}

}
