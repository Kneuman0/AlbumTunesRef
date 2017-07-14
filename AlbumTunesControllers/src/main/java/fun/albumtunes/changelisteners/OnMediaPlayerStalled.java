package fun.albumtunes.changelisteners;

import java.util.List;

import fun.personalUse.dataModel.FileBean;

public class OnMediaPlayerStalled implements Runnable{
	
	private List<FileBean> playlist;
	private FileBean fileBean;
	
	public OnMediaPlayerStalled(List<FileBean> playlist, FileBean fileBean) {
		this.playlist = playlist;
		this.fileBean = fileBean;
	}

	@Override
	public void run() {
		int index = playlist.indexOf(this.fileBean);
		fileBean.setPlayer(null);
		fileBean.setMedia(null);
		System.out.println("Removing: " + playlist.remove(index));
	}

}
