package biz.ui.launchers.generic;

import java.io.File;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import biz.ui.controllers.generic.SigningCredsController;

public class SigningPopupLauncher {
	
	private SigningCredsController controller;
	private Stage stage;
	private File credLoc;
	private String password;
	
	public SigningPopupLauncher(String title, URL fxmlLocation, boolean resizable){
		PopupLauncher<SigningCredsController> popUp = 
				new PopupLauncher<SigningCredsController>(title, fxmlLocation);
		popUp.getStage().setResizable(resizable);
		popUp.show();
		this.controller = popUp.getController();
		this.stage = popUp.getStage();
	}
	
	public class OnOKButtonClicked implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent event) {
			credLoc = controller.getCerdential();
			
		}
		
	}
}
