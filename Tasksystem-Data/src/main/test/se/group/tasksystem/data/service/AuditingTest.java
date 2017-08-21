package se.group.tasksystem.data.service;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.sql.Statement;

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
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.group.tasksystem.data.config.Infrastructure;
import se.group.tasksystem.data.config.TestingInfrastructure;
import se.group.tasksystem.data.exception.ServiceException;
import se.group.tasksystem.data.model.User;
import se.group.tasksystem.data.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Infrastructure.class, TestingInfrastructure.class })
public class AuditingTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static UserService userService;

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
	public void insertAllAuditingValues() throws ServiceException {

		User user = userService.createUser(new User("personalcode", "username15", "firstname", "lastname", null));
		assertEquals(new Long(4L), user.getId());
		assertNotNull(user.getCreatedDate());
		assertNotNull(user.getUpdatedDate());
		assertNotNull(user.getCreatedBy());
		assertNotNull(user.getModifiedBy());
	}

	@Test
	public void updatedDateShouldChangeDate() throws ServiceException {

		User user = userService.getUserById(3L);
		user.setFirstname("new firstname");
		userService.updateUser(user);

		assertNotEquals(user.getCreatedBy(), user.getUpdatedDate());
	}

	@Test
	public void updateShouldChangedModifiedByIfAnotherUser() throws ServiceException {
		User user = userService.getUserById(3L);
		user.setFirstname("changedFirstname");
		User savedUser = userService.updateUser(user);

		assertNotEquals(savedUser.getCreatedBy(), savedUser.getModifiedBy());

	}

	@AfterClass
	public static void tearDown() {
		context.close();
	}
}
