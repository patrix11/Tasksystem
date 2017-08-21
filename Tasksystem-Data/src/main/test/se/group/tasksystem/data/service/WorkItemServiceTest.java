package se.group.tasksystem.data.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import se.group.tasksystem.data.model.WorkItem;
import se.group.tasksystem.data.model.WorkItem.WorkItemStatus;
import se.group.tasksystem.data.service.WorkItemService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Infrastructure.class, TestingInfrastructure.class })
public class WorkItemServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static WorkItemService workItemService;

	private static AnnotationConfigApplicationContext context;

	private static DataSource dataSource;

	@BeforeClass
	public static void setup() {
		context = new AnnotationConfigApplicationContext();
		context.getEnvironment().setActiveProfiles("Development");
		context.register(TestingInfrastructure.class);
		context.scan("se.group.tasksystem.data");
		context.refresh();
		workItemService = context.getBean(WorkItemService.class);

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
	public void getPaginatedWorkItems() {

		Iterable<WorkItem> iterableWorkItemsPageOne = workItemService.getPaginatedWorkItems(new PageRequest(0, 2));
		Iterable<WorkItem> iterableWorkItemsPageTwo = workItemService.getPaginatedWorkItems(new PageRequest(1, 2));

		List<WorkItem> workItemsPageOne = new ArrayList<>();
		List<WorkItem> workItemsPageTwo = new ArrayList<>();

		iterableWorkItemsPageOne.forEach(workItemsPageOne::add);
		iterableWorkItemsPageTwo.forEach(workItemsPageTwo::add);

		assertEquals(new Long(1L), workItemsPageOne.get(0).getId());
		assertEquals(new Long(2L), workItemsPageOne.get(1).getId());
		assertEquals(new Long(3L), workItemsPageTwo.get(0).getId());
	}

	@Test
	public void getWorkItemHistoryStatusBetweenDates() throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date fromDate = sdf.parse("2016-08-01");
		Date toDate = sdf.parse("2016-08-30");
		WorkItemStatus status = WorkItemStatus.DONE;

		List<WorkItem> workItems = workItemService.getWorkItemHistory(fromDate, toDate, status);

		assertEquals(new Long(3L), workItems.get(0).getId());
	}

	@Test
	public void createNewWorkItem() {
		WorkItem workItem = workItemService
				.createWorkItem(new WorkItem("Title", "Description", WorkItemStatus.STARTED));
		assertEquals(new Long(4L), workItem.getId());
	}

	@Test
	public void getFirstWorkItem() {
		WorkItem workItem = workItemService.getWorkItemById(1L);
		assertEquals(new Long(1L), workItem.getId());

	}

	@Test
	public void changeWorkItemStatus() {

		Long workItemId = 1L;
		WorkItemStatus status = WorkItemStatus.DONE;

		WorkItem workItem = workItemService.getWorkItemById(workItemId);
		assertEquals(WorkItemStatus.UNSTARTED, workItem.getStatus());

		WorkItem changedWorkItem = workItemService.changeWorkItemStatus(workItemId, status);
		assertEquals(WorkItemStatus.DONE, changedWorkItem.getStatus());
	}

	@Test
	public void removeWorkItem() throws ServiceException {

		WorkItem workItem = workItemService.createWorkItem(new WorkItem("temp", "temp", WorkItemStatus.STARTED));
		workItemService.removeWorkItem(workItem.getId());

		if (workItemService.getWorkItemById(4L) != null) {
			fail();
		}
	}

	@Test
	public void getAllWorkItemsByStatus() {

		List<WorkItem> workItems = workItemService.getWorkItemsByStatus(WorkItemStatus.DONE);

		assertEquals(new Long(2L), workItems.get(0).getId());
		assertEquals(WorkItemStatus.DONE, workItems.get(0).getStatus());
		assertEquals(new Long(3L), workItems.get(1).getId());
		assertEquals(WorkItemStatus.DONE, workItems.get(1).getStatus());
	}

	@Test
	public void getAllWorkItemsWithIssue() {
		List<WorkItem> workItems = workItemService.getWorkItemsWithIssue();
		assertEquals(new Long(1L), workItems.get(0).getId());
		assertEquals(new Long(2L), workItems.get(1).getId());
	}

	@Test
	public void getWorkItemByDescription() {

		String description = "Description";

		List<WorkItem> workItems = workItemService.getWorkItemsByDescription(description);

		assertEquals(new Long(2L), workItems.get(0).getId());
		assertEquals(new Long(3L), workItems.get(1).getId());
	}

	@Test
	public void getAllWorkItemByUser() {
		List<WorkItem> workItems = workItemService.getAllWorkItemsByUser(2L);
		assertEquals(new Long(1L), workItems.get(0).getId());
	}

	@Test
	public void getAllWorkItemByTeam() {

		List<WorkItem> workItems = workItemService.getAllWorkItemsByTeam(2L);

		assertEquals(new Long(1L), workItems.get(0).getId());
		assertEquals(new Long(2L), workItems.get(1).getId());

		assertEquals(new Long(2L), workItems.get(0).getUser().getTeam().getId());
		assertEquals(new Long(2L), workItems.get(1).getUser().getTeam().getId());
	}

	@AfterClass
	public static void tearDown() {
		context.close();
	}

}
