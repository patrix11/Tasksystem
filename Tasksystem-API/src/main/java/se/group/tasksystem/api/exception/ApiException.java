package se.group.tasksystem.api.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public final class ApiException extends WebApplicationException {

	private static final long serialVersionUID = -6192761879372963627L;

	public ApiException(String message) {
		super(Response.status(Status.BAD_REQUEST).entity(message).build());
	}

}
