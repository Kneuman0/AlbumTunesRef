package biz.ui.controller.utils;

import java.io.File;
import java.util.List;
import java.util.Optional;





import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;

public abstract class ControllerUtils implements IPopupController{
	
	protected Stage stage;
	
	/**
	 * Convienience method for displaying errors to user
	 * @param title displayed at top of window
	 * @param header content displayed right next to symbol
	 * @param content content below symbol (usually null)
	 * @param exp Exception that caused the error
	 */
	public void displayErrorMessage(String title, String header, String content, Exception exp){
		Alert error = new Alert(AlertType.ERROR);
		error.setContentText(content);
		if(exp != null) header += ":\n" + exp.getMessage();
		error.setHeaderText(header);
		error.setTitle(title);
		if(exp != null) exp.printStackTrace();
		error.showAndWait();
	}
	
	public void displayMessage(String title, String header, String content){
		Alert error = new Alert(AlertType.INFORMATION);
		error.setContentText(content);
		error.setHeaderText(header);
		error.setTitle(title);
		error.showAndWait();
	}
	
	public void displaySuccessMessage(String title, String header, String content){
		Alert error = new Alert(AlertType.INFORMATION);
		ImageView view = new ImageView(new Image(getClass().getResourceAsStream("/resources/successCheckMark.png")));
		view.setFitHeight(70);
		view.setFitWidth(70);
		error.setGraphic(view);
		error.setContentText(content);
		error.setHeaderText(header);
		error.setTitle(title);
		error.showAndWait();
	}
	
	public Optional<ButtonType> displayQueryToContinuePrompt(String title, String header, String content){
		Alert error = new Alert(AlertType.CONFIRMATION);
		error.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
		error.setContentText(content);
		error.setHeaderText(header);
		error.setTitle(title);
		return error.showAndWait();
	}
	
	public File requestFile(String title, String initialFileName, ExtensionFilter filter){
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title);
		if(initialFileName != null) chooser.setInitialFileName(initialFileName);
		chooser.getExtensionFilters().add(filter);
		return chooser.showOpenDialog(getWindow());
	}
	
	public File requestFile(String title, String initialFileName, List<ExtensionFilter> filters){
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title);
		if(initialFileName != null) chooser.setInitialFileName(initialFileName);
		chooser.getExtensionFilters().addAll(filters);
		return chooser.showOpenDialog(getWindow());
	}
	
	public List<File> requestFiles(String title, String initialFileName, List<ExtensionFilter> filters){
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title);
		if(initialFileName != null) chooser.setInitialFileName(initialFileName);
		chooser.getExtensionFilters().addAll(filters);
		return chooser.showOpenMultipleDialog(getWindow());
	}
	
	public List<File> requestFiles(String title, String initialFileName, ExtensionFilter filters){
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title);
		if(initialFileName != null) chooser.setInitialFileName(initialFileName);
		chooser.getExtensionFilters().add(filters);
		return chooser.showOpenMultipleDialog(getWindow());
	}
	
	public File requestSave(String title, String initialFileName, ExtensionFilter filters){
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title);
		if(initialFileName != null) 
			chooser.setInitialFileName(initialFileName);
		chooser.getExtensionFilters().add(filters);
		return chooser.showSaveDialog(getWindow());
	}
	
	public File requestSave(String title, String initialFileName){
		return requestSave(title, initialFileName, new ExtensionFilter("All Files", "*.*"));
	}
	
	public File requestSave(String title, String initialFileName, List<ExtensionFilter> filters){
		FileChooser chooser = new FileChooser();
		chooser.setTitle(title);
		if(initialFileName != null) chooser.setInitialFileName(initialFileName);
		chooser.getExtensionFilters().addAll(filters);
		return chooser.showSaveDialog(getWindow());
	}
	
	
	public File requestDirectory(String title, String pathToDir){
		DirectoryChooser chooser = new DirectoryChooser();
		if(pathToDir != null) chooser.setInitialDirectory(new File(pathToDir));
		chooser.setTitle(title);
		return chooser.showDialog(getWindow());
	}
	
	public void setStage(Stage stage){
		this.stage = stage;
	}
	
	
	public Window getWindow(){
		return this.stage.getScene().getWindow();
	}
}
