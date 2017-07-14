package biz.ui.controller.utils;

import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

public abstract class ContentSavedController implements IPopUpSaveController{
	
	public abstract void save();
	
	public EventHandler<WindowEvent> getOnCloseRequest(){
		return new OnCloseRequest();
	}
	
	public class OnCloseRequest implements EventHandler<WindowEvent>{

		@Override
		public void handle(WindowEvent event) {
			save();
		}
		
	}
}
