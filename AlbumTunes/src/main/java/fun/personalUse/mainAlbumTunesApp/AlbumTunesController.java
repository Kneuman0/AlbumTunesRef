package fun.personalUse.mainAlbumTunesApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import biz.albumtunes.textinput.ChangeMetadataAlert;
import biz.albumtunes.textinput.ChangePlaylistAlert;
import biz.albumtunes.textinput.FindNewSongsAlert;
import biz.ui.multithread.utils.UpdateLabel;
import biz.ui.tableview.utils.SelectIndexOnTable;
import fun.albumtunes.changelisteners.MediaPlayerStateChanged;
import fun.albumtunes.changelisteners.OnMainPlaylistChanged;
import fun.albumtunes.changelisteners.OnScrollBarValueChange;
import fun.albumtunes.controllers.MediaEngineController;
import fun.albumtunes.exceptions.NoPlaylistsFoundException;
import fun.albumtunes.utilities.MEDIA_PLAYER_STATE;
import fun.albumtunes.threads.DigSongs;
import fun.personalUse.controllers.MediaViewController;
import fun.personalUse.controllers.PreferencesController;
import fun.personalUse.dataModel.DefaultPrefsBean;
import fun.personalUse.dataModel.FileBean;
import fun.personalUse.dataModel.PlaylistBean;
import fun.personalUse.dataModel.PreferencesBean;
import fun.personalUse.dataModel.PlaylistBean.PlaylistTypes;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class AlbumTunesController extends MediaEngineController{

    @FXML
    private Label 
    	mediaDescLeft,
    	mediaDescRight,
    	digLabel,
    	currentSongTime;

    @FXML
    private Button 
    	playBackButton,
    	nextButton,
    	restartAlbumButton,
    	addPlaylistButton,
    	addSongsToPlaylistButton,
    	mineMP3sButton,
    	launchButton;
        
    @FXML
    private AnchorPane 
    	MediaPlayerAnchorPane,
    	AlbumPlayerAnchorPane;
    
    @FXML
    private TableColumn<FileBean, String> 
    	durationCol,
    	artistCol,
    	songNameCol,
    	albumCol;
    
    @FXML
    private TextField searchBox;

    @FXML
    private TableView<FileBean> metaDataTable;
    
    @FXML
    private TableView<PlaylistBean> playlistTable;

    @FXML
    private TableColumn<PlaylistBean, String> playlistColumn;
    
    @FXML
    private CheckBox shuffleBox;
    
    @FXML
    private ScrollBar songScrollBar;
     
	private Stage currentStage;
	
	private DefaultPrefsBean defaultPrefs;
	
	private PreferencesBean prefs;

	public void initialize() {
		// this will eventually try the info directory first, then read the default as a backup
		loadPreferences();
		
		
		
		
		// index used to keep track of next song
		songNumber = 0;
		
		// loads all the playlist and songs from the XML file and 
		// displays them in TableView objects
		initalizeTableView(prefs.getDefaultParentInfoDirectory());
		songsInAlbum = metaDataTable.getItems();
		songIndexes = new ArrayList<>();
		
		// Highligts the first index in the playlist tableview
		Platform.runLater(new SelectIndexOnTable(playlistTable, 0));
		
		// Highligts the first index in the song tableview
		Platform.runLater(new SelectIndexOnTable(metaDataTable, 0));
		
		initalizeScrollBar();	
	}
	
	public void executePlayback(){
		System.out.println("|" + playBackButton.getText() + "|");
		/*
		 * Media player just opened and nothing has been played yet or album has finished
		 */
		if(playBackButton.getText().equals("Play") && currentPlayer == null){
			playBackButton.setText("Pause");
			songsInAlbum = metaDataTable.getItems();
			startAlbum(metaDataTable.getSelectionModel().getSelectedIndex(), true);
		/*
		 * 	Song is pause, user wants to resume play
		 */
		} else if(playBackButton.getText().equals("Play") && currentPlayer != null){
			playBackButton.setText("Pause");
			currentPlayer.play();
		
		}else if(playBackButton.getText().equals("Pause")){
		
			playBackButton.setText("Play");
			currentPlayer.pause();
		}else{
			// may use for later case
			System.out.println("No previous cases executed");
		}
	}

	public void nextSongButtonListener() {
		if(currentPlayer != null){
			currentPlayer.stop();
			Platform.runLater(new EndOfMediaEventHandler(shuffleIsSelected(), 
					songNumber, songsInAlbum.size(), getSongFileTable()));
		}
		
	}
	
	public void backButtonListener(){
		super.goBackOneSong();
	}
	
	public void restartAlbumButtonListener(){
		// restarts the album from the first index
//		Platform.runLater(new SelectIndexOnTable(metaDataTable, 0));
		metaDataTable.requestFocus();
		metaDataTable.getSelectionModel().clearAndSelect(0);
		metaDataTable.getSelectionModel().focus(0);
		
		// play new song even if paused. If paused, change to label
		if(playBackButton.getText().equals("Play")){
			playBackButton.setText("Pause");
		}
		songNumber = 0;
		startAlbum(0, true);
	}
		
	public void addPlaylistButtonListener(){
		TextInputDialog dialog = new TextInputDialog();
		dialog.setHeaderText("Enter Playlist Name");
		dialog.setTitle("New Playlist");
		dialog.showAndWait();
		
		addPlaylist(dialog.getEditor().getText());
	}
	
	public void addSongsToPlaylistButtonListener(){
		ObservableList<FileBean> songsToBeAdded = 
				metaDataTable.getSelectionModel().getSelectedItems();
		PlaylistBean playlist = playlistTable.getSelectionModel().getSelectedItem();
		playlist.getSongsInPlaylist().addAll(songsToBeAdded);
	}
	
	public void displayPlaylistButtonListener(MouseEvent event){
		if(event.getClickCount() == 2){
			PlaylistBean playlist = playlistTable.getSelectionModel().getSelectedItem();
			metaDataTable.setItems(playlist.getSongsInPlaylist());
			setCurrentPlaylist(playlist.getSongsInPlaylist());
			digLabel.setText("Songs: " + playlist.getSongsInPlaylist().size());
		}
		
		// allow user to change the name of the playlist
		if(event.getButton() == MouseButton.SECONDARY){
			 PlaylistBean playlist = playlistTable.getSelectionModel().getSelectedItem();
			 
			 ChangePlaylistAlert changePlaylistData = new ChangePlaylistAlert(playlist);
			 
			 // if user presses OK, record their changes, otherwise do nothing
			 Optional<ButtonType> result = changePlaylistData.showAndWait();
			 if(result.isPresent()){
				 if(result.get() == ButtonType.OK){
					 playlist.setName(changePlaylistData.getPlaylistName());
				 }
			 }
		}
	}
	
	@SuppressWarnings("unchecked")
	public void playSelectedSong(MouseEvent event){
		 if (event.getClickCount() == 2) {
			 playBackButton.setText("Pause");
			 startAlbum(metaDataTable.getSelectionModel().getSelectedIndex(), true);
	            
	        }
		 
		 /*
		  * If the user right clicks on a song, prompt them with
		  * the option to change the song metadata.
		  */
		 if(event.getButton() == MouseButton.SECONDARY){
			 // make sure the event's source is the TableView
			 TableView<FileBean> table = null;
			 if(event.getSource() instanceof TableView<?>){
				 table = (TableView<FileBean>)event.getSource();
			 }
			 
			 FileBean song = table.getSelectionModel().getSelectedItem();
			 ChangeMetadataAlert changeSongMetaData = new ChangeMetadataAlert(song);
			 
			 
			 // if user presses OK, record their changes, otherwise do nothing
			 Optional<ButtonType> result = changeSongMetaData.showAndWait();
			 if(result.isPresent()){
				 if(result.get() == ButtonType.OK){
					 song.setSongName(changeSongMetaData.getSongName());
					 song.setArtist(changeSongMetaData.getArtist());
					 song.setAlbum(changeSongMetaData.getAlbum());
				 }
			 }
			 
		 }
	}
	
	public void onTableSort(){
		
	}
	
	public void onPlaylistSearch(){
		String search = searchBox.getText();
		if(search.equals("")){
			String playlistName = playlistTable.getSelectionModel().getSelectedItem().getName();
			ObservableList<FileBean> currentPlaylist = getPlaylistByName(playlistName).getSongsInPlaylist();
			
			metaDataTable.setItems(currentPlaylist);
			setCurrentPlaylist(currentPlaylist);
			digLabel.setText("Songs: " + getCurrentPlaylist().size());
		}else{
			ObservableList<FileBean> subList = getsubListFromSearchResult(
					search, metaDataTable.getItems());
			metaDataTable.setItems(subList);
			setCurrentPlaylist(subList);
			digLabel.setText("Songs: " + subList.size());
		}
	}
	
	public void mineMP3sButtonListener(){
		requestFile("Test", null, new ExtensionFilter("Test", "*.tst"));
		metaDataTable.refresh();
		String title = "Please make a selection";
		String header = "mp3 mining options...";
		String content = "Do you want to import a single mp3\n"
				+ "or a folder containing many mp3s?";
		findNewSongs(title, header, content);
	}
	

	
	public void onDeletePlaylist(KeyEvent event){
		if(event.getCode().equals(KeyCode.DELETE)
				|| event.getCode().equals(KeyCode.BACK_SPACE)){
			/**
			 * If user tried to delete the main playlist, tell them they are not allowed
			 */
			if(playlistTable.getSelectionModel().getSelectedItem().getPLAYLIST_TYPE() == PlaylistTypes.MAIN){
				Alert cannotDeleteMainPlaylist = new Alert(AlertType.ERROR);
				cannotDeleteMainPlaylist.setHeaderText(null);
				cannotDeleteMainPlaylist.setContentText("You cannot delete the Main Playlist");
				cannotDeleteMainPlaylist.showAndWait();
				return;
			}
			
			// Make sure user really wants to deleted the selected playlist
			Alert deleteOK = new Alert(AlertType.CONFIRMATION);
			deleteOK.setHeaderText(null);
			deleteOK.setContentText("Are you sure you want to delete this playlist?");
			deleteOK.showAndWait();
			
			if(deleteOK.getResult() == ButtonType.OK){
				int index = playlistTable.getSelectionModel().getSelectedIndex();
				getPlaylists().remove(index);
			}
			
			
		}
	}
	
	public void launchButtonListener(){
		
		Stage stage = new Stage();
		FXMLLoader loader = null;
		Parent parent = null;
		try {
			loader = new FXMLLoader(getClass().getResource("/resources/MediaPlayerGUI.fxml"));
			parent = (Parent)loader.load();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// controller for the application you are about to launch
		MediaViewController controller = (MediaViewController)loader.getController();
		
		/*
		 *  Pass in a reference to the controller that launched it (it's parent), hence 'this'.
		 *  
		 *  Once you have that stored, you can change values in this controller using the 
		 *  other controller, or you can change values in the other controller using this one.
		 *  You have complete freedom.
		 */
		controller.setParentController(this);
		controller.setCurrentStage(stage);
		stage.setOnCloseRequest(controller.getOnExit());
		stage.maximizedProperty().addListener(controller.getOnMaximizedPressed());
		Scene scene = new Scene(parent);
		
		// window title
		stage.setTitle("Video Player");
		stage.setScene(scene);
		stage.show();
	}
	
	public void closeMenuListener(){
		exitApplication();
	}
	
	public void preferencesMenuListener(){
		Stage stage = new Stage();
		FXMLLoader loader = null;
		Parent parent = null;
		try {
			loader = new FXMLLoader(getClass().getResource("/resources/preferencesGUI.fxml"));
			parent = (Parent)loader.load();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// controller for the application you are about to launch
		PreferencesController controller = (PreferencesController)loader.getController();
		
		/*
		 *  Pass in a reference to the controller that launched it (it's parent), hence 'this'.
		 *  
		 *  Once you have that stored, you can change values in this controller using the 
		 *  other controller, or you can change values in the other controller using this one.
		 *  You have complete freedom.
		 */
		controller.setParentController(this);
		controller.setCurrentStage(stage);
		stage.setOnCloseRequest(controller.getOnExit());
		Scene scene = new Scene(parent);
		
		// window title
		stage.setTitle("Preferences");
		stage.setScene(scene);
		stage.show();
	}
	
	
	/**
	 * Attempts to find existing xml files in two parent directories removed from the location
	 * of this Media player's jar file.
	 * 
	 * If no xml's are found, the user will be prompted to locate a directory containing mp3s
	 * so that the program can search for them.
	 */
	private void initalizeTableView(String locationOfPlaylists){
		super.inializeMediaEngine(locationOfPlaylists);
		songNameCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("songName"));
		albumCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("album"));
		artistCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("artist"));
		durationCol.setCellValueFactory(new PropertyValueFactory<FileBean, String>("duration"));
		playlistColumn.setCellValueFactory(new PropertyValueFactory<PlaylistBean, String>("playlistName"));
		
		
		// does not halt program during startup
		Platform.runLater(new LoadAllMusicFiles(metaDataTable));
		
		
		// need to make similar methods for playlist loading
		playlistColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		playlistColumn.setEditable(true);
		metaDataTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}
	
	public void onShuffleSelected(){
		startAlbum(metaDataTable.getSelectionModel().getSelectedIndex(), false);
	}
	
	
	
	/**
	 * @return the currentStage
	 */
	public Stage getCurrentStage() {
		return currentStage;
	}

	/**
	 * @param currentStage the currentStage to set
	 */
	public void setCurrentStage(Stage currentStage) {
		this.currentStage = currentStage;
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
	protected void findNewSongs(String title, String header, String content){
		FindNewSongsAlert newSongs = new FindNewSongsAlert(title, header, content);
		
		Optional<ButtonType> result = newSongs.showAndWait();
		if(newSongs.resultIsSingleMp3(result)){
									
			List<File> selectedFile =  super.requestFiles("Location of mp3s", null, 
					new ExtensionFilter("Audio Files", getSupportedFileTypes()));
			
			if(selectedFile == null){
				return;
			}
			Thread findSongs = new Thread(new DigSongs(selectedFile, getMainPlaylist(), 
					digLabel, metaDataTable));
			findSongs.start();
			
		}else if(newSongs.resultIsFolderOfMp3s(result)){
			DirectoryChooser fileChooser = new DirectoryChooser();
			fileChooser.setTitle("Location to mine for mp3s");
			
			File selectedFile = fileChooser.showDialog(playBackButton.getScene().getWindow());
			List<File> dir = new ArrayList<File>();
			dir.add(selectedFile);
			
			if(selectedFile == null){
				return;
			}
			Thread findSongs = new Thread(new DigSongs(dir, getMainPlaylist(), 
					digLabel, metaDataTable));
			findSongs.start();
			
		}else{
			return;
		}
				
	}
	
	protected void restartPlaySelectedAndShuffle(int startIndex){
		super.restartPlaySelectedAndShuffle(startIndex, 
				metaDataTable.getItems().get(songIndexes.get(0)));
	}
	
	protected void restartDoNotPlaySelectedAndShuffle(){
		int newRandomSongIndex = super.restartAndShuffle();
		FileBean randomSong = null;
		try {
			randomSong = metaDataTable.getItems().get(newRandomSongIndex);
			startNextPlayer(randomSong);
		} catch (IndexOutOfBoundsException e) {
			mediaDescLeft.setText("No Songs");
			mediaDescRight.setText("No Songs");
		}
	}
	
	/*
	 * initiates both the playing or replaying of the album
	 */
	private void startAlbum(int startIndex, boolean playSelectedSong){
		if(currentPlayer != null){
			currentPlayer.stop();
		}
		
		if (shuffleBox.isSelected()) {
			Platform.runLater(new UpdateLabel(mediaDescLeft, "Shuffling..."));
			Platform.runLater(new UpdateLabel(mediaDescRight, "Shuffling..."));
			// de-reference old index list and make a new list
			songIndexes = null;
			songIndexes = new ArrayList<>();
			
			// Start songNumber at the beginning
			
			// get all songs in TableView
			songsInAlbum = metaDataTable.getItems();
			
			// shuffle all songs in playlist but still play selected song
			if(playSelectedSong){
				
				restartPlaySelectedAndShuffle(startIndex);
			/*
			 *  if selected song is not to be played, start with the songs at the
			 *  first shuffled index 
			 */
			}else{
				restartDoNotPlaySelectedAndShuffle();
			}
		// Shuffle box not selected	
		}else{

			FileBean selectedSong = metaDataTable.getSelectionModel().getSelectedItem();
			
			// Start songNumber where selected song is
			this.songNumber = startIndex;
			
			/*
			 *  Get all songs visible to the user
			 */
			songsInAlbum = metaDataTable.getItems();
			
			if(playSelectedSong){
				// start with selected song
				startNextPlayer(selectedSong);
				
			}else{
				// wait for song to end and start unshuffled list with the next song
			}
		}
		

	}


	
	public void setBackgroundImage(String location) throws FileNotFoundException {
		InputStream stream = new FileInputStream(location);
		setBackgroundImage(stream, MediaPlayerAnchorPane);
		setBackgroundImage(stream, AlbumPlayerAnchorPane);
	}
	
	private void loadPreferences(){
		defaultPrefs = readInPreferencesBean(getClass().
				getResourceAsStream("/resources/prefs.xml"));
		try{
			// try to load user defined preferences
			prefs = readInPreferencesBean(defaultPrefs.getDefaultParentInfoDirectory() + "/prefs.xml");
			// if loaded, set shuffle box
			shuffleBox.setSelected(prefs.isShuffle());

			// if background image location is same as default, load as stream
			if(prefs.getBackgroundImageLoc().equals(defaultPrefs.getBackgroundImageLoc())){
				setBackgroundImage(this.getClass().getResourceAsStream(
						defaultPrefs.getBackgroundImageLoc()), AlbumPlayerAnchorPane);
				setBackgroundImage(this.getClass().getResourceAsStream(
						defaultPrefs.getBackgroundImageLoc()), MediaPlayerAnchorPane);
				
			// if default is different, try to set as different image
			}else{
				try {
					setBackgroundImage(prefs.getBackgroundImageLoc());
				} catch (NullPointerException error) {
					setBackgroundImage(this.getClass().getResourceAsStream(
							defaultPrefs.getBackgroundImageLoc()), AlbumPlayerAnchorPane);
					setBackgroundImage(this.getClass().getResourceAsStream(
							defaultPrefs.getBackgroundImageLoc()), MediaPlayerAnchorPane);
				}
			}
			
			
			// if user defined prefs have not been found, set user defined prefs as default
		}catch(FileNotFoundException e){
			prefs = new PreferencesBean();
			prefs.resetAllValues(defaultPrefs);
			resetPrefsToDefault();
		}
	}
	
	public void resetPrefsToDefault(){
		setBackgroundImage(this.getClass().getResourceAsStream(
				defaultPrefs.getBackgroundImageLoc()), AlbumPlayerAnchorPane);
		setBackgroundImage(this.getClass().getResourceAsStream(
				defaultPrefs.getBackgroundImageLoc()), MediaPlayerAnchorPane);
		shuffleBox.setSelected(defaultPrefs.isShuffle());
		prefs.resetAllValues(defaultPrefs);
	}
	
	
	private void initalizeScrollBar(){
		songScrollBar.setMax(1.0);
		songScrollBar.setMin(0.0);
		songScrollBar.setValue(0.0);
		songScrollBar.valueProperty().addListener(
				new OnScrollBarValueChange(currentPlayer, currentSongTime, playBackButton));
	}
	
	public PreferencesBean getPrefs(){
		return prefs;
	}
	

	
	protected SaveEverything saveChanges(){
		return new SaveEverything();
	}
	
	private void exitApplication(){
		if(currentPlayer != null){
			currentPlayer.stop();
		}
		
		// close the window
		getCurrentStage().close();
		// save the xml in the background
		Platform.runLater(new ExitApplication());		
	}
	
	
	public class LoadAllMusicFiles implements Runnable{
		
		private TableView<FileBean> tableView;
		
		public LoadAllMusicFiles(TableView<FileBean> tableView) {
			this.tableView = tableView;
		}	
		
		@Override
		public void run() {
			try {
				loadAllPlaylists();
				tableView.setItems(getMainPlaylist().getSongsInPlaylist());
				getMainPlaylist().getSongsInPlaylist().addListener(new OnMainPlaylistChanged(playlists));
				playlistTable.setItems(getPlaylists());
				digLabel.setText("Complete: " + getCurrentPlaylist().size());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (NoPlaylistsFoundException e) {
				String title = "Mine for mp3s";
				String header = "No playlists were found.\n"
						+ "These are your mp3 mining options...";
				String content = "Do you want to import a single mp3\n"
						+ "or a folder containing many mp3s?\n\n"
						+ "**Note For large volumes of songs this may take a while.\n"
						+ "Grab some coffee or something..**";
				findNewSongs(title, header, content);
				// need to handle file not found exception in new thread
				tableView.setItems(getMainPlaylist().getSongsInPlaylist());
				playlistTable.setItems(getPlaylists());
				Platform.runLater(new SelectIndexOnTable(playlistTable, 0));
				tableView.getSelectionModel().selectFirst();
								
			}
			
		}
		
	}
	
	public class ExportPlaylistsToXML implements Runnable{

		@Override
		public void run() {
			exportPlaylistsToXML();
			
		}
		
	}
	
	
	private class SaveEverything implements EventHandler<WindowEvent>{

		@Override
		public void handle(WindowEvent event) {
			event.consume();
			exitApplication();
		}
		
	}
	
	
	private class ExitApplication implements Runnable{

		@Override
		public void run() {
			prefs.setShuffle(shuffleBox.isSelected());
			exportPlaylistsToXML();
			exportPrefs(prefs, prefs.getInfoDirectoryLocation());
			Platform.exit();
		}
		
	}


	@Override
	public Window getWindow() {
		return searchBox.getScene().getWindow();
	}
	
	public FileBean getcurrentSong(){
		try {
			return new FileBean(super.currentPlayer.getMedia().getSource());
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			super.displayErrorMessage("File Error", "Error with current song", null, e);
			return null;
		}
	}
	
	public class StateChangedListener extends MediaPlayerStateChanged{
		
		
		
		String tempDescLeft = String.format(
				"Now Playing: %s\nArtist: %s", getcurrentSong().getSongName(), getcurrentSong().getArtist());
		String tempDescRight = String.format(
				"Album: %s\nDuration: %.2f", getcurrentSong().getAlbum(), getcurrentSong().getDuration());

		@Override
		public void changed(ObservableValue<? extends MEDIA_PLAYER_STATE> observable, MEDIA_PLAYER_STATE oldValue,
				MEDIA_PLAYER_STATE newValue) {
			if(newValue == MEDIA_PLAYER_STATE.FINISHED){
				Platform.runLater(new UpdateLabel(mediaDescLeft, "Album Finished"));
				Platform.runLater(new UpdateLabel(mediaDescRight, "Album Finished"));
			} else if (newValue == MEDIA_PLAYER_STATE.PAUSED){
				playBackButton.setText("Play");
			}else if (newValue == MEDIA_PLAYER_STATE.PLAYING){
				Platform.runLater(new UpdateLabel(mediaDescLeft, tempDescLeft));
				Platform.runLater(new UpdateLabel(mediaDescRight, tempDescRight));
				playBackButton.setText("Pause");
			}else{
				
			}
		}
		
	}


	@Override
	protected TableView<? extends FileBean> getSongFileTable() {
		return this.metaDataTable;
	}

	@Override
	protected boolean shuffleIsSelected() {
		return this.shuffleBox.isSelected();
	}

	@Override
	protected ScrollBar getScrollBar() {
		return this.songScrollBar;
	}

	@Override
	protected Label getSongTimeLabel() {
		return this.currentSongTime;
	}

}
