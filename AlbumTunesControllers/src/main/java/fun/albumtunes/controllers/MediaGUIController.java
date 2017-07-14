package fun.albumtunes.controllers;

import java.io.InputStream;

import fun.personalUse.dataModel.FileBean;
import fun.personalUse.dataModel.PlaylistBean;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.stage.Window;

public abstract class MediaGUIController extends MediaFilemgmtController {
   
    
	@Override
	public abstract Window getWindow();
	
	
	public void setBackgroundImage(InputStream stream, AnchorPane pane){
		Image logo = new Image(stream);
		BackgroundSize logoSize = new BackgroundSize(600, 400, false, false,
				true, true);
		BackgroundImage image = new BackgroundImage(logo,
				BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.CENTER, logoSize);
		Background background = new Background(image);
		pane.setBackground(background);
	}

}
