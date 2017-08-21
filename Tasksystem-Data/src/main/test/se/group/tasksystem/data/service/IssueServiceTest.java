package se.group.tasksystem.data.service;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.group.tasksystem.data.config.Infrastructure;
import se.group.tasksystem.data.config.TestingInfrastructure;
import se.group.tasksystem.data.exception.ServiceException;
import se.group.tasksystem.data.model.Issue;
import se.group.tasksystem.data.model.WorkItem;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Infrastructure.class, TestingInfrastructure.class })
public class IssueServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static IssueService issueService;
	private static WorkItemService workitemService;

	private static AnnotationConfigApplicationContext context;

	private static DataSource dataSource;

	@BeforeClass
	public static void setup() {
		context = new AnnotationConfigApplicationContext();
		context.getEnvironment().setActiveProfiles("Development");
		context.register(TestingInfrastructure.class);
		context.scan("se.group.tasksystem.data");
		context.refresh();

		issueService = context.getBean(IssueService.class);
		workitemService = context.getBean(WorkItemService.class);

		dataSource = (DataSource) context.getBean("hsqldb");
	}

	@Before
	public void setupDatabase() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("se/group/tasksystem.data/sql/insert-data.sql"));
		DatabasePopulatorUtils.execute(populator, dataSource);
	}

	@After
	public void cleanDatabase() throws SQLException {

		Statement statement = null;

		try {
			statement = dataSource.getConnection().createStatement();
			statement.executeUpdate("TRUNCATE SCHEMA public RESTART IDENTITY AND COMMIT");
		} finally {
			statement.close();
		}
	}

	@Test
	public void getPaginatedIssues() {

		Iterable<Issue> iterableIssuesPageOne = issueService.getPaginatedIssues(new PageRequest(0, 2));
		Iterable<Issue> iterableIssuesPageTwo = issueService.getPaginatedIssues(new PageRequest(1, 2));

		List<Issue> issuesPageOne = new ArrayList<>();
		List<Issue> issuesPageTwo = new ArrayList<>();

		iterableIssuesPageOne.forEach(issuesPageOne::add);
		iterableIssuesPageTwo.forEach(issuesPageTwo::add);

		assertEquals(new Long(1L), issuesPageOne.get(0).getId());
		assertEquals(new Long(2L), issuesPageOne.get(1).getId());
		assertEquals(new Long(3L), issuesPageTwo.get(0).getId());

	}

	@Test
	public void getFirstIssue() {
		Issue issue = issueService.getIssueById(1L);
		assertEquals(new Long(1L), issue.getId());
	}

	@Test
	public void createIssueAndAddToWorkitem() throws ServiceException {
		Issue issue = issueService.createIssueAndAddToWorkItem(new Issue("Title", "Descripton"), 2L);
		WorkItem workitem = workitemService.getWorkItemById(2L);

		assertEquals(new Long(4L), issue.getId());
		assertEquals(new Long(2L), workitem.getId());
	}

	@Test
	public void createIssueAndAddToWorkitemShouldThrowIfWorkitemStatusIsNotDone() throws ServiceException {

		thrown.expect(ServiceException.class);
		thrown.expectMessage("Cannot add issue to workItem because workItem is not status DONE");

		issueService.createIssueAndAddToWorkItem(new Issue("Title", "Descripton"), 1L);
		workitemService.getWorkItemById(2L);
	}

	@AfterClass
	public static void tearDown() {
		context.close();
	}

}
