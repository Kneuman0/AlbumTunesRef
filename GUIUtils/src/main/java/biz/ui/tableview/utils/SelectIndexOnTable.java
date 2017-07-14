package biz.ui.tableview.utils;

import javafx.scene.control.TableView;

public class SelectIndexOnTable implements Runnable {
	private TableView<?> table;
	private int index;

	public SelectIndexOnTable(TableView<?> table, int index) {
		this.table = table;
		this.index = index;
	}

	@Override
	public void run() {
		this.table.requestFocus();
		this.table.getSelectionModel().clearAndSelect(index);
		this.table.getFocusModel().focus(index);
	}
}
