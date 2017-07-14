package biz.albumtunes.textinput;

import biz.ui.features.IndexedGridPane;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;

public abstract class TextInputAlert extends Alert{
	
	protected IndexedGridPane grid;

	public TextInputAlert() {
		super(AlertType.CONFIRMATION);
		
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		
		// add text fields to Alert box
		super.getDialogPane().setContent(grid);
	}
	
	public IndexedGridPane getGridPane(){
		return grid;
	}

}
