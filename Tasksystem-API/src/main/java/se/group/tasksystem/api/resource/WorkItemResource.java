package se.group.tasksystem.api.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import se.group.tasksystem.api.model.WorkItemRequestBean;
import se.group.tasksystem.data.exception.ServiceException;
import se.group.tasksystem.data.model.WorkItem;
import se.group.tasksystem.data.service.UserService;
import se.group.tasksystem.data.service.WorkItemService;

@Component
@Path("workitems")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class WorkItemResource {

	@Autowired
	WorkItemService workItemService;

	@Autowired
	UserService userService;

	@Context
	UriInfo uriInfo;

	@POST
	public Response createWorkItem(WorkItem workItem) {
		WorkItem newWorkItem = new WorkItem(workItem.getTitle(), workItem.getDescription(), workItem.getStatus());
		try {
			workItemService.createWorkItem(newWorkItem);
			URI location = uriInfo.getAbsolutePathBuilder().path(newWorkItem.getId().toString()).build();
			return Response.created(location).build();
		} catch (RuntimeException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@PUT
	public Response changeWorkItemStatus(WorkItem workItem) {
		// TODO Check if workItemstatus is valid
		workItemService.changeWorkItemStatus(workItem.getId(), workItem.getStatus());
		URI location = uriInfo.getAbsolutePathBuilder().path(workItem.getId().toString()).build();
		return Response.created(location).build();
	}

	@GET
	public List<WorkItem> getWorkItemsBy(@BeanParam WorkItemRequestBean request) {

		if (request.getStatus() != null) {
			return workItemService.getWorkItemsByStatus(request.getStatus());
		}
		if (request.getDescription() != null) {
			return workItemService.getWorkItemsByDescription(request.getDescription());
		}

		return workItemService.getAllWorkitem();
	}

	@DELETE
	@Path("{id}")
	public Response deleteWorkItem(@PathParam("id") Long id) {
		try {
			workItemService.removeWorkItem(id);
			return Response.status(Status.NO_CONTENT).build();
		} catch (ServiceException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@PUT
	@Path("{workItemId}/user/{userId}")
	public Response addWorkItemToUser(@PathParam("workItemId") Long workItemId, @PathParam("userId") Long userId) {
		try {
			userService.addWorkItemToUser(userId, workItemId);
			return Response.status(Status.NO_CONTENT).build();
		} catch (ServiceException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@GET
	@Path("team/{teamId}")
	public List<WorkItem> getWorkItemsByTeam(@PathParam("teamId") Long teamId) {
		return workItemService.getAllWorkItemsByTeam(teamId);
	}

	@GET
	@Path("user/{userId}")
	public List<WorkItem> getWorkItemsByUser(@PathParam("userId") Long userId) {
		return workItemService.getAllWorkItemsByUser(userId);
	}

}
