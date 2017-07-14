package biz.ui.controllers.generic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;
import biz.ui.controller.utils.ControllerUtils;
import biz.ui.controller.utils.IPopupController;

public class SigningCredsController extends ControllerUtils implements IPopupController{
	
    @FXML
    private TextField passwordTextField, certLocationTextField;
	
	private Stage stage;
	
	public void initialize(){
		// deliberately blank
	}
	
	
	public void browseButtonListener(){
		File cred = requestFile("Signing Credential Location", null, getSupportedSigningFilters());
		if(cred != null){
			certLocationTextField.setText(cred.getAbsolutePath());
		}
	}
	
	@Override
	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	public File getCerdential(){
		return new File(certLocationTextField.getText());
	}
	
	public String getPassword(){
		return passwordTextField.getText();
	}


	@Override
	public Window getWindow() {
		// TODO Auto-generated method stub
		return passwordTextField.getScene().getWindow();
	}
	
	private List<ExtensionFilter> getSupportedSigningFilters(){
		List<ExtensionFilter> filters = new ArrayList<FileChooser.ExtensionFilter>();
		filters.add(new ExtensionFilter("PFX File", "*.pfx"));
		filters.add(new ExtensionFilter("PKSC12 File", "*.p12"));
		return filters;
	}
	
}
