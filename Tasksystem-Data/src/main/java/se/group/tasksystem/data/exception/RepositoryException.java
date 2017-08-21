package se.group.tasksystem.data.exception;

public class RepositoryException extends RuntimeException {

	private static final long serialVersionUID = 6613396450428733377L;

	public RepositoryException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public RepositoryException(String message) {
		super(message);
	}

}
