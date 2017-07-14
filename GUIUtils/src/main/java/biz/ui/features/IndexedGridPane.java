package biz.ui.features;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;

public class IndexedGridPane extends GridPane{
		
		
		
		public  Node get(final int column, final int row) {
		    Node result = null;
		    ObservableList<Node> childrens = getChildren();

		    for (Node node : childrens) {
		        if(getColumnIndex(node) == column && getRowIndex(node) == row) {
		            result = node;
		            break;
		        }
		    }

		    return result;
		}
		
		public int getRowCount() {
	        int numRows = getRowConstraints().size();
	        for (int i = 0; i < getChildren().size(); i++) {
	            Node child = getChildren().get(i);
	            if (child.isManaged()) {
	                Integer rowIndex = GridPane.getRowIndex(child);
	                if(rowIndex != null){
	                    numRows = Math.max(numRows,rowIndex+1);
	                }
	            }
	        }
	        return numRows;
	    }
}

