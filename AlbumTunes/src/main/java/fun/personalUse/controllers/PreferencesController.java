package fun.personalUse.controllers;

import java.io.File;
import java.io.FileNotFoundException;

import biz.ui.controller.utils.ControllerUtils;
import fun.albumtunes.controllers.MediaEngineController;
import fun.albumtunes.controllers.XMLMediaController;
import fun.personalUse.dataModel.DefaultPrefsBean;
import fun.personalUse.dataModel.FileBean;
import fun.personalUse.mainAlbumTunesApp.AlbumTunesController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class PreferencesController extends XMLMediaController{
	


    @FXML
    private TextField 
    	imageLocTextField,
    	storageDirectoryTextField;

    @FXML
    private Button 
    	dataStoreageChangeButton,
    	imageLocChangeButton,
    	backupPlaylistDataButton,
    	resetDefualtsButton;
    
    private DefaultPrefsBean defaultPrefBean;
    
    private AlbumTunesController parentController;
    private Stage currentStage;
    
    
    public void initialize(){
    	defaultPrefBean = readInDefaultPrefBean();
    	System.out.println(defaultPrefBean.toString());
    }
    
    public void dataStoreageChangeButtonListener(){
    	
    }
    
    public void imageLocChangeButtonListener(){
    	System.out.println("| " + imageLocTextField.getText() + " |");
    	if(!imageLocTextField.getText().equals("")){
    		try {
    			parentController.setBackgroundImage(imageLocTextField.getText());
    			parentController.getPrefs().setBackgroundImageLoc(imageLocTextField.getText());
    			return;
    		} catch (FileNotFoundException e) {
    			Alert alert = new Alert(AlertType.ERROR);
    			alert.setTitle("Error Loading File");
    			alert.setHeaderText(null);
    			alert.setContentText("The image you selected could not be loaded");
    			setDefaultBackground();
    			alert.showAndWait();
    			return;
    		}
    	}
    	FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Location of background image");
		
		File selectedFile = fileChooser.showOpenDialog(imageLocChangeButton.getScene().getWindow());
		
		if (selectedFile == null){
			return;
		}
		
    	try {
			parentController.setBackgroundImage(selectedFile.getAbsolutePath());
			imageLocTextField.setText(selectedFile.getAbsolutePath());
			parentController.getPrefs().setBackgroundImageLoc(selectedFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Loading File");
			alert.setHeaderText(null);
			alert.setContentText("The image you selected could not be loaded");
			setDefaultBackground();
			alert.showAndWait();
			return;
		}
    }
    
    public void backupPlaylistButtonListener(){
    	DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("Location to backup playlist data");
		
		File selectedFile = fileChooser.showDialog(imageLocTextField.getScene().getWindow());
		
		if(selectedFile == null){
			return;
		}
		
		Platform.runLater(new BackUpData(selectedFile.getAbsolutePath()));
    }
    
    public void returnToDefaultSettings(){
    	parentController.resetPrefsToDefault();
    	setTextFields();
    }
    
    private DefaultPrefsBean readInDefaultPrefBean(){    	
    	return readInPreferencesBean(getClass().getResourceAsStream("/resources/prefs.xml")); 
    }
        
    public void setParentController(AlbumTunesController controller){
    	parentController = controller;
    	setTextFields();
    }
    
    public void setCurrentStage(Stage currentStage){
    	this.currentStage = currentStage;
    }
        
    private void setTextFields(){
    	if(!parentController.getPrefs().getBackgroundImageLoc().equals(defaultPrefBean.getBackgroundImageLoc())){
    		imageLocTextField.setText(parentController.getPrefs().getBackgroundImageLoc());
    	}else{
    		imageLocTextField.setText("");
    	}
    	
    	if(!parentController.getPrefs().getInfoDirectoryLocation().equals(defaultPrefBean.getInfoDirectoryLocation())){
    		storageDirectoryTextField.setText(parentController.getPrefs().getBackgroundImageLoc());
    	}else{
    		storageDirectoryTextField.setText("");
    	}
    }
    
    public ExitListener getOnExit(){
    	return new ExitListener();
    }
    
    private class ExitListener implements EventHandler<WindowEvent>{

		@Override
		public void handle(WindowEvent event) {
					
			currentStage.close();
			
		}
		
	}
    
    private class BackUpData implements Runnable{
    	
    	String exportLocation;
    	
    	public BackUpData(String location) {
			this.exportLocation = location;
		}
    	
		@Override
		public void run() {
			exportPlaylistsToXML(exportLocation, parentController.getPlaylists());
		}
    	
    }
    
    /**
     * Need to figure out which anchor (if not both
     */
    private void setDefaultBackground(){
    	try {
			parentController.setBackgroundImage(this.getClass().
					getResource(defaultPrefBean.getBackgroundImageLoc()).toString());
		} catch (FileNotFoundException e) {
			displayErrorMessage("Background File Not Found",
					String.format("File name: %s", defaultPrefBean.getBackgroundImageLoc()), null, e);
		}
    }

	@Override
	public Window getWindow() {
		return imageLocTextField.getScene().getWindow();
	}    

}
