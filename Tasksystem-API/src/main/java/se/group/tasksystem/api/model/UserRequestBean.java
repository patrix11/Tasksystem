package se.group.tasksystem.api.model;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public final class UserRequestBean {

	@QueryParam("username")
	@DefaultValue("")
	private String username;

	@QueryParam("firstname")
	@DefaultValue("")
	private String firstname;

	@QueryParam("lastname")
	@DefaultValue("")
	private String lastname;

	public String getUsername() {
		return username;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

}
