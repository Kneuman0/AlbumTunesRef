package fun.albumtunes.controllers;

import java.text.DecimalFormat;
import java.util.ArrayList;

import biz.ui.controller.utils.ControllerUtils;
import javafx.stage.Window;

public abstract class MediaFilemgmtController extends ControllerUtils {

	
	public abstract Window getWindow();
	
	// cut and paste to utilities controller later
	public static String convertDecimalMinutesToTimeMinutes(double minutes){
		DecimalFormat time = new DecimalFormat("00");
		int fullMinutes = (int)minutes;
		int secondsRemainder = (int)((minutes - fullMinutes) * 60);
		return String.format("%d.%s", fullMinutes, time.format(secondsRemainder));
	}
	
	protected ArrayList<String> getSupportedFileTypes(){
		ArrayList<String> extensions = new ArrayList<>();
		extensions.add("*.MP4");
		extensions.add("*.mp4");
		extensions.add("*.M4V");
		extensions.add("*.m4v");
		extensions.add("*.M4A");
		extensions.add("*.m4a");
		extensions.add("*.mp3");
		extensions.add("*.MP3");
		return extensions;
	}

}
