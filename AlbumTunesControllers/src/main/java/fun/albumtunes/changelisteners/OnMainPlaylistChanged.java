package fun.albumtunes.changelisteners;

import java.util.List;

import fun.personalUse.dataModel.FileBean;
import fun.personalUse.dataModel.PlaylistBean;
import fun.personalUse.dataModel.PlaylistBean.PlaylistTypes;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class OnMainPlaylistChanged implements ListChangeListener<FileBean>{
	
	private List<PlaylistBean> playlists;
	
	public OnMainPlaylistChanged(List<PlaylistBean> playlists) {
		this.playlists = playlists;
	}
	
	@Override
	public void onChanged(
			javafx.collections.ListChangeListener.Change<? extends FileBean> c) {
		int i = 0;
		while(c.next()){
			
			if(!c.wasRemoved()){
				// Not removed type. Continue to next item
				i++;
				continue;
			}
			
			
			for(PlaylistBean playlist : playlists){
				
				// if the change was from anything other than removal, do nothing
				FileBean removed = c.getRemoved().get(i);
				
				
				
				// only look in user defined playlists 
				if(playlist.getPLAYLIST_TYPE() == PlaylistTypes.USER_DEFINED){
					ObservableList<FileBean> temp = playlist.getSongsInPlaylist();
					
					// remove the song deleted from the main playlist from all others
					try{
						temp.remove(temp.indexOf(removed));
					}catch(ArrayIndexOutOfBoundsException e){
						// cannot find item in array
					}
					
				}
			}
			i++;
		}
		
		
	}
	

}
