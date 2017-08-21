package se.group.tasksystem.api.resource;

import java.net.URI;
import java.util.List;

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
import se.group.tasksystem.data.exception.ServiceException;
import se.group.tasksystem.data.model.Team;
import se.group.tasksystem.data.service.TeamService;

@Component
@Path("teams")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class TeamResource {

	@Autowired
	TeamService teamService;

	@Context
	UriInfo uriInfo;

	@POST
	public Response createTeam(Team team) {
		Team newTeam = new Team(team.getName());
		try {
			teamService.createTeam(newTeam);
			URI location = uriInfo.getAbsolutePathBuilder().path(newTeam.getId().toString()).build();
			return Response.created(location).build();
		} catch (RuntimeException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@PUT
	public Response updateTeam(Team team) {
		try {
			teamService.updateTeam(team);
			URI location = uriInfo.getAbsolutePathBuilder().path(team.getId().toString()).build();
			return Response.created(location).build();
		} catch (RuntimeException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@PUT
	@Path("inactivate/{id}")
	public Response inActivateTeam(@PathParam("id") Long id) {
		try {
			teamService.inActivateTeam(id);
			return Response.status(Status.NO_CONTENT).build();
		} catch (ServiceException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@PUT
	@Path("activate/{id}")
	public Response activateTeam(@PathParam("id") Long id) {
		try {
			teamService.activateTeam(id);
			return Response.status(Status.NO_CONTENT).build();
		} catch (ServiceException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@GET
	public List<Team> getTeams() {
		return teamService.getAllTeams();
	}

	@PUT
	@Path("user/{userId}")
	public Response addUserToTeam(@PathParam("userId") Long userId) {
		try {
			Team team = teamService.addUserToTeam(userId);
			URI location = uriInfo.getAbsolutePathBuilder().path(team.getId().toString()).build();
			return Response.created(location).build();
		} catch (ServiceException e) {
			throw new ApiException(e.getMessage());
		}
	}

}
