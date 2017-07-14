package biz.ui.features;

import java.util.Collections;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class UpArrow {
	
	public class UpButtonTreeTable extends Button {
		private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-padding: 2, 2, 2, 2;";
    
	    public UpButtonTreeTable(double width, double height) {
	    	ImageView view = new ImageView(new Image(getClass().getResourceAsStream(
					"/resources/down.png")));
	    	view.setFitWidth(width);
	    	view.setFitHeight(height);
	        setGraphic(view);
	        setStyle(STYLE_NORMAL);
	        
	                
	        setOnMouseReleased(new EventHandler<MouseEvent>() {
	            @Override
	            public void handle(MouseEvent event) {
	               setStyle(STYLE_NORMAL);
	            }            
	        });
	        
	        setOnAction(new OnMoveUp());
	        
	    }
	    
		public class OnMoveUp implements EventHandler<ActionEvent>{
			 private final String STYLE_PRESSED = "-fx-background-color: transparent; -fx-padding: 3 1 1 3;";

			@SuppressWarnings("unchecked")
			@Override
			public void handle(ActionEvent event) {
				Button button = (Button)event.getSource();
				button.setStyle(STYLE_PRESSED);
				
				// get the table
				TreeTableView<Object> table = (TreeTableView<Object>)
						button.getParent().getParent().getParent()
						.getParent().getParent().getParent().getParent();
				
				// get the item
				TreeTableRow<Object> row = (TreeTableRow<Object>)
						button.getParent().getParent().getParent();
				Object itemValue  = row.getItem();
				TreeItem<Object> item = row.getTreeItem();
				
				ObservableList<TreeItem<Object>> list = item.getParent().getChildren();
				
				if(item == null || list == null || list.size() <= 1) return;
				
				// swap the current item with the one above it
				int index = getIndexOf(list, itemValue);
				if(index > 0){
					Collections.swap(list, index, index-1);
					table.getSelectionModel().clearAndSelect(index-1);
				}
			}
			
			private int getIndexOf(List<TreeItem<Object>> items, Object bean){
				int index = -1;
				for(int i = 0; i < items.size(); i++){
					if(items.get(i).getValue().equals(bean))
						return i;
				}
				
				return index;
			}
			
		}
    
	}
	
	

	

}


