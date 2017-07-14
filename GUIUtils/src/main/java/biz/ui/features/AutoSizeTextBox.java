package biz.ui.features;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

public class AutoSizeTextBox extends TextField{
	
	public AutoSizeTextBox(String contents){
		setMinWidth(Region.USE_PREF_SIZE);
		setMaxWidth(Region.USE_PREF_SIZE);
		textProperty().addListener(new AutoAdjustText());
		setText(contents);
	}

	private class AutoAdjustText implements ChangeListener<String>{

		@Override
		public void changed(ObservableValue<? extends String> ov,
				String prevText, String currText) {
			Platform.runLater(() -> {
		        Text text = new Text(currText);
		        text.setFont(getFont()); // Set the same font, so the size is the same
		        double width = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
		                + getPadding().getLeft() + getPadding().getRight() // Add the padding of the TextField
		                + 2d; // Add some spacing
		        
		        if(width < 5) width = 20;
		        setPrefWidth(width + 25); // Set the width
		        positionCaret(getCaretPosition()); // If you remove this line, it flashes a little bit
		    });
			
		}
		
	}
}
