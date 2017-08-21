package se.group.tasksystem.data.exception;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = -1654934775714970360L;

	public ServiceException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public ServiceException(String message) {
		super(message);
	}

}
