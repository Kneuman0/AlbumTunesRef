package biz.ui.controller.utils;

import javafx.scene.control.Label;

public class TimedTextDisplay implements Runnable {
	
	private String startVal;
	private String endVal;
	private Label label;
	private long time;
	
	public TimedTextDisplay(String startVal, String endVal, Label label, long time){
		this.time = time;
		this.startVal = startVal;
		this.endVal = endVal;
		this.label = label;
	}

	@Override
	public void run() {
		label.setText(startVal);
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		label.setText(endVal);
	}
	
	

}
