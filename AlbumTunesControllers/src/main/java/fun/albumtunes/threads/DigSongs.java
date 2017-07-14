package fun.albumtunes.threads;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import biz.ui.multithread.utils.UpdateLabel;
import fun.albumtunes.changelisteners.OnMediaPlayerStalled;
import fun.personalUse.dataModel.FileBean;
import fun.personalUse.dataModel.PlaylistBean;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

public class DigSongs implements Runnable{
	private List<File> files;
	private Label notifyLabel;
	private PlaylistBean main;
	private TableView<? extends FileBean> table;
	
	public DigSongs(List<File> files, PlaylistBean main, 
			Label notifyLabel,  TableView<? extends FileBean> table) {
		this.files = files;
		this.notifyLabel = notifyLabel;
		this.main = main;
		this.table = table;
	}
	@Override
	public void run() {
		Platform.runLater(new UpdateLabel(notifyLabel, "loading..."));
		
		for(File file : files){
			try {
				// add new songs to existing main playlist
				digSongs(main.getSongsInPlaylist(), file);
				
				removeDuplicates(main.getSongsInPlaylist());

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		ObservableList<FileBean> songArray = main.getSongsInPlaylist();
		Platform.runLater(new UpdateLabel(notifyLabel, "complete: " + songArray.size()));
	}
	
	public List<FileBean> digSongs (File directory) throws FileNotFoundException, UnsupportedEncodingException{
		ObservableList<FileBean> files = FXCollections.observableArrayList();
		return digSongs(files, directory);
	}
	
	/**
	 * method used to seek all mp3 files in a specified directory and save them
	 * to an ObservableArrayList
	 * 
	 * @param existingSongs
	 * @param directory
	 * @return
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	protected ObservableList<FileBean> digSongs(ObservableList<FileBean> existingSongs,
			File directory) throws FileNotFoundException,
			UnsupportedEncodingException {
		/*
		 * Each directory is broken into a list and passed back into the digSongs().
		 */
		if (directory.isDirectory() && directory.canRead()) {

			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				digSongs(existingSongs, files[i]);
			}
			
			/*
			 * if a file is not a directory, then is it checked to see if it's
			 * an mp3 file
			 */
		} else if (directory.getAbsolutePath().endsWith(".mp3") 
				|| directory.getAbsolutePath().endsWith(".m4a")
				) {
			FileBean songBean = new FileBean(directory).getSerializableJavaBean();
			
			existingSongs.add(songBean);
			
			/**
			 * dont think we need this
			 */
//			songBean.getPlayer().setOnReady(new OnMediaReadyEvent(songBean, table));
			songBean.getPlayer().setOnError(new OnMediaPlayerStalled(existingSongs, songBean));
//			
			/*
			 * if it's not a directory or mp3 file, then do nothing
			 */
		} else {

			return existingSongs;

		}

		return existingSongs;
	}
	
	public ObservableList<FileBean> removeDuplicates(ObservableList<FileBean> playlist){
		for(int i = 0; i < playlist.size(); i++){
			FileBean temp = playlist.remove(i);
			int duplicateIndex = playlist.indexOf(temp);
			
			while(duplicateIndex != -1){
				playlist.remove(duplicateIndex);
				duplicateIndex = playlist.indexOf(temp);
			}
			playlist.add(i, temp);
		}
		
		return playlist;
	}
}
