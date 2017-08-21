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
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import se.group.tasksystem.api.exception.ApiException;
import se.group.tasksystem.data.exception.ServiceException;
import se.group.tasksystem.data.model.Issue;
import se.group.tasksystem.data.model.WorkItem;
import se.group.tasksystem.data.service.IssueService;
import se.group.tasksystem.data.service.WorkItemService;

@Component
@Path("issues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class IssueResource {

	@Autowired
	IssueService issueService;

	@Autowired
	WorkItemService workitemService;

	@Context
	UriInfo uriInfo;

	@POST
	@Path("workitem/{workitemId}")
	public Response createIssueAndAddToWorkItem(@PathParam("workitemId") Long workitemId, Issue issue) {
		try {
			Issue newIssue = issueService.createIssueAndAddToWorkItem(issue, workitemId);
			URI location = uriInfo.getAbsolutePathBuilder().path(newIssue.getId().toString()).build();
			return Response.created(location).build();
		} catch (ServiceException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@PUT
	public Response updateIssue(Issue issue) {
		try {
			issueService.updateIssue(issue);
			return Response.ok().build();
		} catch (ServiceException e) {
			throw new ApiException(e.getMessage());
		}
	}

	@GET
	@Path("workitems")
	public List<WorkItem> getAllWorkItemWithIssue() {
		return workitemService.getWorkItemsWithIssue();
	}

	@GET
	public List<Issue> getAllIssue() {
		return issueService.getAllIssue();
	}

}
