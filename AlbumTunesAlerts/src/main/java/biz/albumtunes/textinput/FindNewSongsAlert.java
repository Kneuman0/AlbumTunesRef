package biz.albumtunes.textinput;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;

public class FindNewSongsAlert extends Alert{
	
	protected ButtonType singleMp3;
	protected ButtonType folderOfmp3s;
	protected ButtonType cancel;

	public FindNewSongsAlert(String title, String header, String content) {
		super(AlertType.CONFIRMATION);
		
		super.setTitle(title);
		super.setHeaderText(header);
		super.setContentText(content);
		
		ButtonType singleMp3 = new ButtonType("Single mp3");
		ButtonType folderOfmp3s = new ButtonType("Folder Of mp3s");
		ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		super.getButtonTypes().setAll(singleMp3, folderOfmp3s, cancel);
	}
	
	public boolean resultIsSingleMp3(Optional<ButtonType> result){
		return result.get() == singleMp3;
	}
	
	public boolean resultIsFolderOfMp3s(Optional<ButtonType> result){
		return result.get() == folderOfmp3s;
	}
	
	public boolean resultIsCancel(Optional<ButtonType> result){
		return result.get() == cancel;
	}

	public ButtonType getSingleMp3() {
		return singleMp3;
	}

	public void setSingleMp3(ButtonType singleMp3) {
		this.singleMp3 = singleMp3;
	}

	public ButtonType getFolderOfmp3s() {
		return folderOfmp3s;
	}

	public void setFolderOfmp3s(ButtonType folderOfmp3s) {
		this.folderOfmp3s = folderOfmp3s;
	}

	public ButtonType getCancel() {
		return cancel;
	}

	public void setCancel(ButtonType cancel) {
		this.cancel = cancel;
	}

}
