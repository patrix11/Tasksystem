package se.group.tasksystem.api.model;

import javax.ws.rs.QueryParam;

import se.group.tasksystem.data.model.WorkItem.WorkItemStatus;

public final class WorkItemRequestBean {

	@QueryParam("status")
	private WorkItemStatus status;

	@QueryParam("description")
	private String description;

	public WorkItemStatus getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}

}
