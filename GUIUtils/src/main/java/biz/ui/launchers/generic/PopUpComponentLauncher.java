package biz.ui.launchers.generic;

import java.io.IOException;
import java.net.URL;

import biz.ui.controller.utils.IPopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

public class PopUpComponentLauncher <Controller extends IPopupController, ParentNode extends Node>{
	
	private Controller controller;
	private ParentNode parent;
	
	public PopUpComponentLauncher(URL fxmlLocation){
		FXMLLoader loader = null;
		parent = null;
		try {
			// Reads in the FXML file and initializes the fields in the
			// controller/GUI
			loader = new FXMLLoader(fxmlLocation);
			parent = loader.<ParentNode> load();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Retrieves the instance of the controller
		this.controller = loader
				.<Controller> getController();

	}
	
	public Controller getController(){
		return controller;
	}
	
	public ParentNode getParent(){
		return parent;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}
	
	
}
