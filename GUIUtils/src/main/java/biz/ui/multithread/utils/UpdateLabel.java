package biz.ui.multithread.utils;

import javafx.scene.control.Label;

public class UpdateLabel implements Runnable {
	Label label;
	String textToUpdate;

	public UpdateLabel(Label label, String textToUpdate) {
		this.label = label;
		this.textToUpdate = textToUpdate;
	}

	@Override
	public void run() {
		label.setText(textToUpdate);
	}
}
