package fun.personalUse.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Optional;

import fun.albumtunes.utilities.MediaUtils;
import fun.personalUse.dataModel.FileBean;
import fun.personalUse.mainAlbumTunesApp.AlbumTunesController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class MediaViewController {
	
    @FXML
    private MediaView mediaView;
    
    @FXML
    private ScrollBar videoScrollBar;
    
    @FXML
    private Label currentMediaTime;
    
    private AlbumTunesController parentCont;    
    private Stage currentStage;
    private MediaPlayer currentPlayer;
	
	public void initialize(){
		inializeScrollBar();
		bindMediaView();
	}
	
	public void findNewVideosListener(){
		String title = "Search For mp4s";
		String header = "Please make a selection";
		String content = "Select 'Single mp4 to search for \n"
				+ "a video to watch";
		findNewSongs(title, header, content);
	}
	
	public void pauseListener(){
		currentPlayer.pause();
	}
	
	public void resumeListener(){
		currentPlayer.play();
	}
	
	public void stopListener(){
		currentPlayer.stop();
	}
	
	public void maximizeButtonListener(){
		currentStage.setMaximized(true);
	}
	
	public void minimizedListener(KeyEvent event){
		if(currentStage.isMaximized() && event.getCode() == KeyCode.ESCAPE){
			Platform.exit();
		}
	}
	
	public void setCurrentStage(Stage currentStage){
		this.currentStage = currentStage;
	}
	
	public ExitListener getOnExit(){
		return new ExitListener();
	}
	
	public OnMaximizedPressed getOnMaximizedPressed(){
		return new OnMaximizedPressed();
	}
	
	/**
	 * this allows you to change value in the parent controller if you wish.
	 * 
	 * You could also opt to store this controllers reference instead in the parent
	 * controller. In that case you could use Simple___Property classes that have
	 * registered listeners in the parent controller to know when a value changes
	 * in this child controller.
	 * @param parentController
	 */
	public void setParentController(AlbumTunesController parentController){
		this.parentCont = parentController;
	}
	
	public AlbumTunesController getParentController(){
		return parentCont;
	}
	
	private void inializeScrollBar(){
		videoScrollBar.setMin(0.0);
		videoScrollBar.setMax(1.0);
		videoScrollBar.setValue(0.0);
		videoScrollBar.valueProperty().addListener(new OnScrollBarValueChange());
	}
	
	private class ExitListener implements EventHandler<WindowEvent>{

		@Override
		public void handle(WindowEvent event) {
			if(currentPlayer != null){
				currentPlayer.stop();
			}
			
			currentStage.close();
			
		}
		
	}
	
	private void bindMediaView(){
		
		DoubleProperty mediaViewHeight = mediaView.fitHeightProperty();
		DoubleProperty mediaViewWidth = mediaView.fitWidthProperty();
		mediaViewHeight.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
		mediaViewWidth.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
		mediaView.setPreserveRatio(false);
	}
	
	/**
	 * The method will display an Alert box prompting the user to locate a 
	 * song or directory that contains mp3s
	 * 
	 * The parameters passed is the text the user will see in the Alert box.
	 * The Alert box will come with 3 new buttons: 1)Single mp3, 2)Folder of mp3s
	 * and 3)Cancel. If the user selects the first button they will be
	 * presented with a FileChooser display to select a song. If they press
	 * the second button, the user will be prompted with a DirectoryChooser
	 * display. The third button displays nothing and closes the Alert box.
	 * 
	 * The following outlines where each parameter will be displayed in the
	 * Alert box
	 * 
	 * title: very top of the box in the same latitude as the close button.
	 * header: inside the Alert box at the top.
	 * content: in the middle of the box. This is the best place to explain
	 * the button options to the user.
	 * @param title
	 * @param header
	 * @param content
	 */
	private void findNewSongs(String title, String header, String content){
		Alert importType = new Alert(AlertType.CONFIRMATION);
		importType.setTitle(title);
		importType.setHeaderText(header);
		importType.setContentText(content);
		
		ButtonType singleMp3 = new ButtonType("Single Video");
		ButtonType cancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		importType.getButtonTypes().setAll(singleMp3, cancel);
		
		Optional<ButtonType> result = importType.showAndWait();
		if(result.get() == singleMp3){
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Location of Videos");
			
			fileChooser.getExtensionFilters().add(new ExtensionFilter("Audio Files", getSupportedFileTypes()));
			
			File selectedFile = fileChooser.showOpenDialog(currentStage.getScene().getWindow());
			
			if(selectedFile == null){
				return;
			}else if(acceptedVideoType(selectedFile.getAbsolutePath())){
				FileBean fileBean = null;
				try {
					fileBean = new FileBean(selectedFile.getAbsolutePath());
				} catch (FileNotFoundException | UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				playAVideo(fileBean);
				
			// if not supported file type, return
			}else{
				return;
			}
			
		// if Cancel, return			
		}else{
			return;
		}
	}
	
	private boolean acceptedVideoType(String absolutePath){
		return absolutePath.endsWith(".mp4") || absolutePath.endsWith(".MP4")
				|| absolutePath.endsWith(".m4v") || absolutePath.endsWith(".M4V")
				|| absolutePath.endsWith(".m4a") || absolutePath.endsWith(".M4A");
	}
	
	private void playAVideo(FileBean fileBean){
		currentPlayer = fileBean.getPlayer();
		currentPlayer.setAudioSpectrumListener(new OnMediaProgressUpdate());
		currentPlayer.setAutoPlay(true);
		mediaView.setMediaPlayer(currentPlayer);
	}
	
	private ArrayList<String> getSupportedFileTypes(){
		ArrayList<String> extensions = new ArrayList<>();
		extensions.add("*.MP4");
		extensions.add("*.mp4");
		extensions.add("*.M4V");
		extensions.add("*.m4v");
		extensions.add("*.M4A");
		extensions.add("*.m4a");
		return extensions;
	}
	
	private class OnScrollBarValueChange implements ChangeListener<Number>{

		@Override
		public void changed(ObservableValue<? extends Number> observable,
				Number oldValue, Number newValue) {
			
			double ratio = currentPlayer.getCurrentTime().toMinutes()/
					currentPlayer.getCycleDuration().toMinutes();
			
			if(!String.format("%.5f", newValue.doubleValue()).equals(String.format("%.5f", ratio))){
				
				// pause media player so it doesn't trigger an
				// event with the equalizer property thing.. may not be necessary
				currentPlayer.pause();
				
				// get time relative to scroll bar in milliseconds
				double minutes = currentPlayer.getCycleDuration().toMillis() * newValue.doubleValue();
				
				// convert to minutes and display above scroll bar
				String time = MediaUtils.convertDecimalMinutesToTimeMinutes(minutes/60000.0);
				currentMediaTime.setText(time);
				
				// seek to the new found song location and play it
				Duration duration = new Duration(minutes);
				currentPlayer.seek(duration);
				currentPlayer.play();
			}
			
			
		}
	
	}
	
	private class OnMediaProgressUpdate implements javafx.scene.media.AudioSpectrumListener{

		@Override
		public void spectrumDataUpdate(double timestamp, double duration,
				float[] magnitudes, float[] phases) {
			double scrollBarValue = currentPlayer.getCurrentTime().toSeconds()/
					currentPlayer.getTotalDuration().toSeconds();
			videoScrollBar.setValue(scrollBarValue);		
			currentMediaTime.setText(MediaUtils.convertDecimalMinutesToTimeMinutes(
					(currentPlayer.getCurrentTime().toMinutes())));
		}
		
	}
	
	private class OnMaximizedPressed implements ChangeListener<Boolean>{

		@Override
		public void changed(ObservableValue<? extends Boolean> observable,
				Boolean oldValue, Boolean newValue) {
			currentStage.setMaximized(newValue);
		}

				
	}
}
	


