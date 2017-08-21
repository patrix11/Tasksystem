package se.group.tasksystem.api.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.group.tasksystem.api.exception.ApiException;
import se.group.tasksystem.api.model.UserRequestBean;
import se.group.tasksystem.data.exception.ServiceException;
import se.group.tasksystem.data.model.User;
import se.group.tasksystem.data.service.UserService;

@Component
@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public final class UserResource {

	@Autowired
	UserService userService;

	@Context
	UriInfo uriInfo;

	@POST
	public Response createUser(User user) {
		System.out.println(user.getUsername());
		try {
			User createdUser = userService.createUser(user);
			URI location = uriInfo.getAbsolutePathBuilder().path(createdUser.getId().toString()).build();
			return Response.created(location).build();
		} catch (ServiceException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@PUT
	public Response updateUser(User user) {
		try {
			User updatedUser = userService.updateUser(user);
			URI location = uriInfo.getAbsolutePathBuilder().path(updatedUser.getId().toString()).build();
			return Response.ok(location).build();
		} catch (ServiceException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@PUT
	@Path("inactivate/{id}")
	public Response inActivateUser(@PathParam("id") Long id) {
		try {
			userService.inActivateUser(id);
			return Response.status(Status.NO_CONTENT).build();
		} catch (ServiceException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@PUT
	@Path("activate/{id}")
	public Response activateUser(@PathParam("id") Long id) {
		try {
			userService.activateUser(id);
			return Response.status(Status.NO_CONTENT).build();
		} catch (ServiceException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@GET
	@Path("{id}")
	public User getUserById(@PathParam("id") Long id) {
		try {
			return userService.getUserById(id);
		} catch (ServiceException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@GET
	@Path("personalcode/{personalcode}")
	public User getUserByPersonalCode(@PathParam("personalcode") String code) {
		return userService.getUserByPersonalCode(code);
	}

	@GET
	public List<User> getUsersBy(@BeanParam UserRequestBean request) {
		List<User> users = userService.getUsersBy(request.getUsername(), request.getFirstname(), request.getLastname());
		return users;
	}

	@GET
	@Path("team/{teamId}")
	public List<User> getUsersFromTeam(@PathParam("teamId") Long teamId) {
		List<User> users = userService.getAllUserFromTeam(teamId);
		return users;
	}

}
