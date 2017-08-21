package se.group.tasksystem.data;

import java.text.ParseException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import se.group.tasksystem.data.config.Infrastructure;
import se.group.tasksystem.data.exception.ServiceException;
import se.group.tasksystem.data.model.Issue;
import se.group.tasksystem.data.model.Team;
import se.group.tasksystem.data.service.IssueService;
import se.group.tasksystem.data.service.TeamService;
import se.group.tasksystem.data.service.UserService;
import se.group.tasksystem.data.service.WorkItemService;

public final class Main {

	public static void main(String[] args) throws ServiceException, ParseException {

		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.getEnvironment().setActiveProfiles("Production");
		context.register(Infrastructure.class);
		context.scan("se.group.tasksystem.data");
		context.refresh();

		UserService userService = context.getBean(UserService.class);
		TeamService teamService = context.getBean(TeamService.class);
		WorkItemService workitemService = context.getBean(WorkItemService.class);
		IssueService issueService = context.getBean(IssueService.class);

	}

}

