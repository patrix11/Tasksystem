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
import se.group.tasksystem.data.model.User;
import se.group.tasksystem.data.model.WorkItem;
import se.group.tasksystem.data.model.WorkItem.WorkItemStatus;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Infrastructure.class, TestingInfrastructure.class })

public class UserServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static UserService userService;
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
		userService = context.getBean(UserService.class);

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
	public void getPaginatedUsers() {

		Iterable<User> iterableUsersPageOne = userService.getPaginatedUsers(new PageRequest(0, 2));
		Iterable<User> iterableUsersPageTwo = userService.getPaginatedUsers(new PageRequest(1, 2));

		List<User> usersPageOne = new ArrayList<>();
		List<User> usersPageTwo = new ArrayList<>();

		iterableUsersPageOne.forEach(usersPageOne::add);
		iterableUsersPageTwo.forEach(usersPageTwo::add);

		assertEquals(new Long(1L), usersPageOne.get(0).getId());
		assertEquals(new Long(2L), usersPageOne.get(1).getId());
		assertEquals(new Long(3L), usersPageTwo.get(0).getId());

	}

	@Test
	public void getFirstUser() {

		User user = userService.getUserById(1L);
		assertEquals("firstname1", user.getFirstname());
	}

	@Test
	public void createNewUser() throws ServiceException {

		User newUser = userService.createUser(new User("personalcode", "firstname4", "eric", "karlsson", null));
		assertEquals(new Long(4L), newUser.getId());
	}

	@Test
	public void createNewUserShouldThrowIfUsernameIsLessThanTenCharachters() throws ServiceException {

		thrown.expect(ServiceException.class);
		thrown.expectMessage("Username too short, must be 10 characters");

		userService.createUser(new User("personalcode", "erix10111", "eric", "karlsson", null));
	}

	@Test
	public void updateSecondUser() throws ServiceException {

		User user2 = userService.getUserById(2L);
		assertEquals("firstname2", user2.getFirstname());

		user2.setFirstname("firstname2 updated");
		userService.updateUser(user2);
		assertEquals("firstname2 updated", user2.getFirstname());
	}

	@Test
	public void getUserByUsernameAndFirstnameAndLastname() {

		List<User> usernameResult = userService.getUsersBy("username11", "", "");
		List<User> firstnameResult = userService.getUsersBy("", "firstname2", "");
		List<User> lastnameResult = userService.getUsersBy("", "", "lastname");
		List<User> allFieldResult = userService.getUsersBy("username13", "firstname3", "lastname");
		assertEquals(new Long(1L), usernameResult.get(0).getId());
		assertEquals(new Long(2L), firstnameResult.get(0).getId());
		assertEquals(new Long(1L), lastnameResult.get(0).getId());
		assertEquals(new Long(2L), lastnameResult.get(1).getId());
		assertEquals(new Long(3L), lastnameResult.get(2).getId());
		assertEquals(new Long(3L), allFieldResult.get(0).getId());
	}

	@Test
	public void getAllUserFromTeam() {
		List<User> users = userService.getAllUserFromTeam(2L);
		assertEquals(new Long(2L), users.get(0).getId());
		assertEquals(new Long(3L), users.get(1).getId());
	}

	@Test
	public void addWorkItemToUserShouldThrowIfUserDoesNotExist() throws ServiceException {

		thrown.expect(ServiceException.class);
		thrown.expectMessage("User does not exist");

		userService.addWorkItemToUser(5L, 1L);
	}

	@Test
	public void addWorkItemToUserShouldThrowIfUserIsInActive() throws ServiceException {

		thrown.expect(ServiceException.class);
		thrown.expectMessage("Could not add workItem to user because its inactive");
		userService.inActivateUser(3L);
		userService.addWorkItemToUser(3L, 1L);
	}

	@Test
	public void addWorkItemToUserShouldThrowIfUserHasFiveWorkItems() throws ServiceException {

		for (int i = 1; i <= 5; i++) {
			WorkItem workItem = workItemService
					.createWorkItem(new WorkItem("title " + i, "desc", WorkItemStatus.STARTED));
			userService.addWorkItemToUser(1L, workItem.getId());
		}

		thrown.expect(ServiceException.class);
		thrown.expectMessage("Could not add workItem to user because user already has 5 workItems");

		userService.addWorkItemToUser(1L, 1L);
	}

	@Test
	public void addWorkItemToUser() throws ServiceException {

		userService.addWorkItemToUser(2L, 1L);
		userService.addWorkItemToUser(2L, 2L);
		WorkItem assignedWorkItem = workItemService.getWorkItemById(1L);
		assertEquals(new Long(2L), assignedWorkItem.getUser().getId());
	}

	@Test
	public void inActivateUser() {

		User inActivatedUser = userService.inActivateUser(2L);
		assertEquals(new Long(2L), inActivatedUser.getId());
		assertEquals(false, inActivatedUser.getIsActive());

		List<WorkItem> changedWorkItems = workItemService.getAllWorkItemsByUser(2L);

		for (WorkItem workItem : changedWorkItems) {
			assertEquals(WorkItemStatus.UNSTARTED, workItem.getStatus());
		}
	}

	@Test
	public void activateUser() throws ServiceException {
		User inActivatedUser = userService.getUserById(3L);
		assertEquals(false, inActivatedUser.getIsActive());

		User activatedUser = userService.activateUser(inActivatedUser.getId());
		assertEquals(true, activatedUser.getIsActive());

	}

	@AfterClass
	public static void tearDown() {
		context.close();
	}

	// @PostConstruct
	// public void displayHSQLDB() throws InterruptedException {
	// DatabaseManagerSwing
	// .main(new String[] { "--url", "jdbc:hsqldb:mem:spring-ordersystem",
	// "--user", "sa", "--password", "" });
	// while (true) {
	// Thread.sleep(0);
	// }
	// }

}
