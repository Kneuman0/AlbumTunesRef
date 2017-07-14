package fun.albumtunes.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import biz.personalAcademics.lib.pathClasses.PathGetter;
import biz.ui.multithread.utils.UpdateLabel;
import biz.ui.tableview.utils.SelectIndexOnTable;
import fun.albumtunes.changelisteners.OnMainPlaylistChanged;
import fun.albumtunes.changelisteners.OnMediaProgressUpdate;
import fun.albumtunes.changelisteners.OnMediaStopped;
import fun.albumtunes.exceptions.InvalidUserInputException;
import fun.albumtunes.exceptions.NoPlaylistsFoundException;
import fun.albumtunes.utilities.MEDIA_PLAYER_STATE;
import fun.personalUse.dataModel.FileBean;
import fun.personalUse.dataModel.PlaylistBean;
import fun.personalUse.dataModel.PlaylistBeanMain;
import fun.personalUse.dataModel.PlaylistBean.PlaylistTypes;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Window;

public abstract class MediaEngineController extends XMLMediaController{
	
	
	protected ObservableList<PlaylistBean> playlists;
	protected File file;
	protected String xmlDirectory;
	protected ObservableList<FileBean> currentPlaylist;
	
	public MediaPlayer currentPlayer;
	protected List<FileBean> songsInAlbum;
	protected ArrayList<Integer> songIndexes;
	
	/*
	 * is incremented each time a song is played
	 */
	protected int songNumber;
		
	protected ObjectProperty<MEDIA_PLAYER_STATE> playerState;
	
	
	/**
	 * Should be the default constructor used. This constructor will
	 * look in the parent directory of the parent directory containing the jar file 
	 * version of this program.
	 */
	public void inializeMediaEngine(){
		playlists = FXCollections.observableArrayList();
		PathGetter pathGetter = new PathGetter(this);
		String parentDirectory = pathGetter.getAbsoluteSubfolderPath();
		String[] folders = parentDirectory.split("/");
		String parentOfParent = "";
		for(int i = 0; i < (folders.length - 1); i++){
			parentOfParent += folders[i] + "/";
		}
		parentOfParent += "Media_Player_5000/infoDirectory/";
		
		if(parentOfParent == "/Media Player 5000/infoDirectory"){
			parentOfParent = parentDirectory + "Media_Player_5000/infoDirectory/";
		}
		
		System.out.printf("Parent: %s, ParentOfParent: %s\n", parentDirectory, parentOfParent);
		File file = new File(parentOfParent);
		this.file = file;
		
		if(file.mkdirs()){
			System.out.println("Directory sucessfully created");
		}else{
			System.out.println("Directory not created");
		}
		
		this.setXmlDirectory(parentOfParent);
	}
	
	protected MediaPlayer getCurrentPlayer(){
		return currentPlayer;
	}
	
	/**
	 * Initializes the object with the location to the directory
	 * containing the XML files
	 * @param directoryPathToXMLs
	 */
	public void inializeMediaEngine(String directoryPathToXMLs){
		super.initializeMediaDir(directoryPathToXMLs);
		playlists = FXCollections.observableArrayList();
		file = new File(directoryPathToXMLs);
		this.xmlDirectory = directoryPathToXMLs;
	}
	
	/**
	 * This method will return an aboservable list of playlist that have been loaded from an XML
	 * in the infoDirectory
	 * @return
	 * @throws FileNotFoundException
	 * @throws NoPlaylistsFoundException
	 */
	public void loadAllPlaylists() 
			throws FileNotFoundException, NoPlaylistsFoundException{
		
		
		File[] playlistXMLs = null;
		if(file.isDirectory() && file.canRead()){
			playlistXMLs = file.listFiles();
		}else{
			throw new NoPlaylistsFoundException();
		}
		
		boolean playlistXMLDoesNotExist = true;
		for(File xml : playlistXMLs){
			if(xml.getAbsolutePath().endsWith("playlists.xml")){
				playlists = super.importPlaylists(xml);
				playlistXMLDoesNotExist = false;
			}
		}
		
		if(playlistXMLDoesNotExist){
			throw new NoPlaylistsFoundException();
		}
		
		/**
		 * Always sets the current playlist to to the main playlist upon loading
		 */
		currentPlaylist = getMainPlaylist().getSongsInPlaylist();
		
	}
	
	
	/**
	 * Searches through the entire playlist for songs that match the search.
	 * 
	 * The search keys off of the song title, album name, and artist
	 * @param search
	 * @param playlist
	 * @return
	 */
	public ObservableList<FileBean> getsubListFromSearchResult(String search, 
			ObservableList<FileBean> playlist){
		setCurrentPlaylist(playlist);
		ObservableList<FileBean> subList = FXCollections.observableArrayList();
		
		for(FileBean song : playlist){
			if(song.toString().toLowerCase().contains(search.toLowerCase().trim())){
				subList.add(song);
			}
		}
		
		return subList;
	}
	
	/**
	 * Returns the main playlist from a list of playlists. The main
	 * playlist contains all the music in the media player.
	 * @return
	 */
	public PlaylistBean getMainPlaylist(){
		PlaylistBean temp = new PlaylistBeanMain();
		
		boolean noMainPlaylist = true;
		for(PlaylistBean main : playlists){
			if(main.getPLAYLIST_TYPE() == PlaylistTypes.MAIN){
				temp = main;
				noMainPlaylist = false;
			}
		}
		
		if(noMainPlaylist){
			// created a new main playlist and adds it to the playlists ObservableList
			temp = makeMainPlaylistBean();
		}
		
		return temp;
	}
	
	/**
	 * Returns the first playlist in the list of playlist that matches the name passed in
	 * 
	 * If no playlist matches the name, InvalidUserInputException is thrown
	 * @param name
	 * @return
	 * @throws InvalidUserInputException
	 */
	public PlaylistBean getPlaylistByName(String name) throws InvalidUserInputException{
		PlaylistBean list = null;
		for(PlaylistBean playlist : playlists){
			if(playlist.getName().equals(name)){
				list = playlist;
				break;
			}else{
				throw new InvalidUserInputException("No playlist found by that name");
			}
		}
		
		return list;
	}
	
	/**
	 * Adds a new PlaylistBean to the playlist ObservableList with the specifed name
	 * @param name
	 */
	public void addPlaylist(String name){
		PlaylistBean newPlaylist = new PlaylistBean();
		newPlaylist.setName(name);
		playlists.add(newPlaylist);
	}


	/**
	 * @return the playlists
	 */
	public ObservableList<PlaylistBean> getPlaylists() {
		return playlists;
	}


	/**
	 * @param playlists the playlists to set
	 */
	public void setPlaylists(ObservableList<PlaylistBean> playlists) {
		this.playlists = playlists;
	}


	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}


	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}
	
	public void exportPlaylistsToXML(){
		super.exportPlaylistsToXML(xmlDirectory, playlists);
	}


	/**
	 * @return the xmlDirectory
	 */
	public String getXmlDirectory() {
		return xmlDirectory;
	}


	/**
	 * @param xmlDirectory the xmlDirectory to set
	 */
	public void setXmlDirectory(String xmlDirectory) {
		this.xmlDirectory = xmlDirectory;
	}
	
	


	/**
	 * @return the currentPlaylist
	 */
	public ObservableList<FileBean> getCurrentPlaylist() {
		return currentPlaylist;
	}

	/**
	 * @param currentPlaylist the currentPlaylist to set
	 */
	public void setCurrentPlaylist(ObservableList<FileBean> currentPlaylist) {
		this.currentPlaylist = currentPlaylist;
	}

	/**
	 * Creates a new main playlist which should contain all the songs.
	 * This method should not be used if a main playlist already exists.
	 * @return
	 */
	protected PlaylistBean makeMainPlaylistBean(){
		PlaylistBean main = new PlaylistBeanMain();
		ObservableList<FileBean> observableSongs = FXCollections.observableArrayList();
		main.setName("All Music");
		main.setSongsInPlaylist(observableSongs);
		playlists.add(main);
		setCurrentPlaylist(main.getSongsInPlaylist());
		main.getSongsInPlaylist().addListener(new OnMainPlaylistChanged(playlists));
		return main;
	}
	
	public static String convertDecimalMinutesToTimeMinutes(double minutes){
		DecimalFormat time = new DecimalFormat("00");
		int fullMinutes = (int)minutes;
		int secondsRemainder = (int)((minutes - fullMinutes) * 60);
		return String.format("%d.%s", fullMinutes, time.format(secondsRemainder));
	}
	
	public ObservableList<FileBean> getDeepCopyOfPlaylist(ObservableList<FileBean> playlist){
		ObservableList<FileBean> temp = FXCollections.observableArrayList();
		for(FileBean song : playlist){
			FileBean bean = new FileBean();
			bean.setAlbum(song.getAlbum());
			bean.setArtist(song.getArtist());
			bean.setDuration(song.getDuration());
			bean.setLocation(song.getLocation());
			bean.setSongName(song.getSongName());
			bean.setUrl(song.getUrl());
			temp.add(bean);
		}
		return temp;
	}
	
	
	protected abstract TableView<? extends FileBean> getSongFileTable();
	
	protected abstract boolean shuffleIsSelected();
	
	protected abstract ScrollBar getScrollBar();
	
	protected abstract Label getSongTimeLabel();
	/**
	 * Creates a new media player by using the FileBean passed in and
	 * stores that media player in the currentPlayer variable.
	 * 
	 * @param songFile
	 * @return
	 */
	protected void playASong(FileBean songFile) {
		Media song = new Media(songFile.getUrl());
		if(currentPlayer != null){
			currentPlayer.dispose();
		}
		currentPlayer = new MediaPlayer(song);
		
		currentPlayer.setOnEndOfMedia(new EndOfMediaEventHandler(shuffleIsSelected(), 
				songNumber, songsInAlbum.size(), getSongFileTable()));
		currentPlayer.setOnStopped(new OnMediaStopped(currentPlayer));
		currentPlayer.setAudioSpectrumInterval(1.0);
		currentPlayer.setAudioSpectrumListener(new OnMediaProgressUpdate(currentPlayer,
				getScrollBar(), getSongTimeLabel()));
		
		/*
		 * The media takes some time to load so you need to resister 
		 * a listener with the MediaPlayer object to commence playing
		 * once the status is switched to READY
		 */
		currentPlayer.setOnReady(new OnMediaReadyEvent(songFile, getSongFileTable()));
	}
	
	public void onDeleteSong(KeyEvent event){
		if(event.getCode().equals(KeyCode.DELETE)
				|| event.getCode().equals(KeyCode.BACK_SPACE)){
			
			ObservableList<FileBean> songsToDelete = FXCollections.observableArrayList(
					getSongFileTable().getSelectionModel().getSelectedItems());
			Collections.reverse(songsToDelete);
			
			for(FileBean song : songsToDelete){
				// remove song from playlist
				int index = getCurrentPlaylist().indexOf(song);
				FileBean deletedSong = getCurrentPlaylist()
						.remove(index);
				
				// remove song from 

				// if one of the deleted songs was playing, iterate to the next song
				if(currentPlayer != null){
					String currentSongURL = currentPlayer.getMedia().getSource();
					System.out.printf("currentSongURL:%s\n%s\n", currentSongURL, deletedSong.getUrl());
					if(currentSongURL.equals("file:///" + deletedSong.getUrl())){
						currentPlayer.stop();
						Platform.runLater(new EndOfMediaEventHandler(shuffleIsSelected(), 
								songNumber, songsInAlbum.size(), getSongFileTable()));
					}
				}
				
			}
			
		}
	}
	
	public void goBackOneSong(){
		if (currentPlayer == null){
			return;
		}
		
		if(!getSongTimeLabel().getText().equals("0.00")){
			getScrollBar().setValue(0.0);
		}else if(getSongTimeLabel().getText().equals("0.00") && currentPlayer != null
				&& songNumber >= 2){
			songNumber -= 2;
			currentPlayer.stop();
			Platform.runLater(new EndOfMediaEventHandler(shuffleIsSelected(), 
					songNumber, songsInAlbum.size(), getSongFileTable()));
		}else{
			// do nothing because player has not been started
			return;
		}
	}
	
	protected void startNextPlayer(FileBean selectedSong){
		if(currentPlayer == null){
			playASong(selectedSong);
		}else{
			currentPlayer.stop();
			playASong(selectedSong);
		}
	}
	
	protected void restartPlaySelectedAndShuffle(int startIndex, FileBean selectedSong){
		songNumber = 0;
		/*
		 * Add all indexes of current TableView of songs except the current song
		 */
		for(int i = 0; i < songsInAlbum.size(); i++){
			if(i != startIndex){
				songIndexes.add(new Integer(i));
			}
			
		}
		
		// shuffle all indexes. Better than using Random because there will be no repeats
		Collections.shuffle(songIndexes);
		// play selected song
		songIndexes.add(0, new Integer(startIndex));
		
		startNextPlayer(selectedSong);
	}
	
	protected int restartAndShuffle() throws IndexOutOfBoundsException{
		songNumber = 0;
		// add all indexes
		for(int i = 0; i < songsInAlbum.size(); i++){
			songIndexes.add(new Integer(i));					
		}
		
		// shuffle all indexes. Better than using Random because there will be no repeats
		Collections.shuffle(songIndexes);
		return songIndexes.get(0);
	}

	@Override
	public abstract Window getWindow();
	
	public class OnMediaReadyEvent implements Runnable{
		private FileBean songFile;
		private TableView<? extends FileBean> table;
		
		public OnMediaReadyEvent(FileBean songFile, TableView<? extends FileBean> table) {
			this.songFile = songFile;
			this.table = table;
		}
		
		@Override
		public void run() {
			
			
						
			songFile.setDuration(songFile.getDuration());
			playerState.set(MEDIA_PLAYER_STATE.PLAYING);
			Platform.runLater(new SelectIndexOnTable(table, table.getItems().indexOf(songFile)));
			currentPlayer.play();
			
			/*
			 * if the playback button reads "Play," the player is paused. re-pause the player for the next song
			 */
			if(playerState.get() == MEDIA_PLAYER_STATE.PAUSED){
				currentPlayer.pause();
			}
			
			// increments index variable 'songNumber' each time playASong() is called
			songNumber++;
		}
		
	}

	protected class EndOfMediaEventHandler implements Runnable {
		
		private boolean shuffle;
		private int songNumber, totalSongs;
		private TableView<? extends FileBean> table;
		
		public EndOfMediaEventHandler(boolean shuffle,
				int songNumber, int totalSongs, TableView<? extends FileBean> table) {
			this.shuffle = shuffle;
			this.songNumber = songNumber;
			this.totalSongs = totalSongs;
			this.table = table;
		}

		@Override
		public void run() {
			// if shuffle is on
			if(shuffle && songNumber < totalSongs){
				
				int nextRandomIndex = songIndexes.get(songNumber);
				FileBean nextSong = table.getItems().get(nextRandomIndex);
				playASong(nextSong);
			
			// if shuffle is off
			}else if(!shuffle && songNumber < totalSongs){
				
				FileBean nextSong = table.getItems().get(songNumber);
				playASong(nextSong);
				
			}else{
				playerState.set(MEDIA_PLAYER_STATE.FINISHED);
			}

		}

	}
	
}
