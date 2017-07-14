package fun.albumtunes.exceptions;

@SuppressWarnings("serial")
public class NoPlaylistsFoundException extends RuntimeException{
	
	public NoPlaylistsFoundException(){
		super("No Playlists were found in infoDirectory");
	}

}
