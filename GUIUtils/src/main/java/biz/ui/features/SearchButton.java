package biz.ui.features;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public abstract class SearchButton extends Button{
	
    private final String STYLE_NORMAL = "-fx-background-color: transparent; -fx-padding: 2, 2, 2, 2;";
    
    public SearchButton() {
    	ImageView image = new ImageView(new Image(getClass().getResourceAsStream("/resources/search.png")));
    	image.setFitHeight(15);
    	image.setFitWidth(15);
        setGraphic(image);
        setStyle(STYLE_NORMAL);
        
                
        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
               setStyle(STYLE_NORMAL);
               executeLogic(event);
            }            
        });
    }
    
    public abstract void executeLogic(MouseEvent event);
}
