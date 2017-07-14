package biz.ui.launchers.generic;

import java.io.IOException;
import java.net.URL;

import biz.ui.controller.utils.IPopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PopupLauncher <Controller extends IPopupController>{
	
	Controller controller;
	Stage stage;
	
	public PopupLauncher(Stage stage, String title, URL fxmlLocation){
		this.stage = stage;
		FXMLLoader loader = null;
		Parent parent = null;
		try {
			// Reads in the FXML file and initializes the fields in the
			// controller/GUI
			loader = new FXMLLoader(fxmlLocation);
			parent = loader.<Parent> load();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Retrieves the instance of the controller
		this.controller = loader
				.<Controller> getController();
		controller.setStage(this.stage);

		Scene scene = new Scene(parent);

		// window title
		stage.setTitle(title);
		stage.setScene(scene);
	}
	
	public PopupLauncher(String title, URL fxmlLocation){
		this(new Stage(), title, fxmlLocation);		
	}
	

	
	public Controller getController(){
		return controller;
	}
	
	public void show(){
		stage.show();
	}
	
	public Stage getStage(){
		return stage;
	}
}
