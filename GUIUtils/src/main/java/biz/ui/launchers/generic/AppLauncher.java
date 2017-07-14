package biz.ui.launchers.generic;

import biz.ui.controller.utils.IPopupController;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Extends this class when creating a new application. All you need to do is pass in the
 * relative path to the fxml file (including the controller and the extension).
 * 
 * use this with a main method launcher class
 * @author karottop
 *
 * @param <Controller>
 */
public abstract class AppLauncher <Controller extends IPopupController> extends Application{

	/**
	 * Override this method and return the relative path to fxml file.
	 * for example:
	 * return "/resources/someFXMLfile.fxml"
	 * @param relPathToController
	 */
	public abstract String getPathtoFXML();
	
	/**
	 * return the title of the stage for the main application
	 * @return
	 */
	public abstract String getStageTitle();
	
	public abstract void init();
	
	
	public void start(Stage stage){
		PopupLauncher<Controller> mainWindow = new PopupLauncher<Controller>
		(stage, getStageTitle(), getClass().getResource(getPathtoFXML()));
	
		mainWindow.getStage().setResizable(true);
					
		mainWindow.show();
	}

}
