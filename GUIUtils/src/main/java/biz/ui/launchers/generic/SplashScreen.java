package biz.ui.launchers.generic;

import biz.ui.controller.utils.IPopupController;

import javafx.application.Preloader;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class SplashScreen <Controller extends IPopupController> extends Preloader{
	
	/**
	 * Field for statically closing the stage once the application launches
	 */
	private static Stage preloaderStage;

	/**
	 * method for initializing the splash screen
	 * The stage will be undecorated: no window border or min/max/close buttons
	 */
	@Override
	public void start(Stage stage) throws Exception {
		PopupLauncher<Controller> splashscrn = new PopupLauncher<Controller>
			(stage, null, getClass().getResource(fxmlLocation()));
		
		splashscrn.getStage().initStyle(StageStyle.UNDECORATED);
		preloaderStage = splashscrn.getStage();
		splashscrn.show();
	}
	  
	/**
	 * Method needed to define the literal location of the fxml file.
	 * Because this class is called through reflection, this information cannot be
	 * passed into a constructor. The implemented method should simply return a string 
	 * literal of the relatitve path to the fxml file. The string will be used:
	 * 
	 * <code>getClass().getResource(fxmlLocation);<code>
	 * @return relative path to fxmlLocation
	 */
	protected abstract String fxmlLocation();
	
	public static void closeSplashScreen(){
		preloaderStage.close();
	}
	
}
