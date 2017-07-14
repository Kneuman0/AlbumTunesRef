package fun.albumtunes.exceptions;

@SuppressWarnings("serial")
public class InvalidUserInputException extends RuntimeException{
	
	public InvalidUserInputException(String path) {
		super(path + "is not valid");
	}

}
