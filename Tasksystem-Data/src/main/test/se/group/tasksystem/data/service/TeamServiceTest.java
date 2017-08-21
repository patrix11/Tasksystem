package se.group.tasksystem.data.service;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.sql.Statement;
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
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.group.tasksystem.data.config.Infrastructure;
import se.group.tasksystem.data.config.TestingInfrastructure;
import se.group.tasksystem.data.exception.ServiceException;
import se.group.tasksystem.data.model.Team;
import se.group.tasksystem.data.model.User;
import se.group.tasksystem.data.service.TeamService;
import se.group.tasksystem.data.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Infrastructure.class, TestingInfrastructure.class })
public class TeamServiceTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static TeamService teamService;
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

		teamService = context.getBean(TeamService.class);
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
	public void getFirstTeam() {
		Team team = teamService.getTeamById(1L);
		assertEquals(new Long(1L), team.getId());
	}

	@Test
	public void createNewTeam() {
		Team newTeam = teamService.createTeam(new Team("team 4"));
		assertEquals(new Long(4L), newTeam.getId());
	}

	@Test
	public void addUserToTeam() throws ServiceException {
		Long userId = 1L;

		teamService.addUserToTeam(userId);
		User user = userService.getUserById(userId);
		assertEquals(new Long(1L), user.getTeam().getId());
	}

	@Test
	public void addUserToTeamShouldThrowIfUserIsInActive() throws ServiceException {

		thrown.expect(ServiceException.class);
		thrown.expectMessage("Could not add user to team since it's inactive");
		teamService.addUserToTeam(3L);
	}

	@Test
	public void addUserToTeamShouldSkipIfTeamHasTenUsers() throws ServiceException {

		Team team1 = teamService.getTeamById(1L);

		for (int i = 1; i <= 10; i++) {
			userService.createUser(new User("personalcode", "username " + i, "firstname", "lastname", team1));
		}

		User newUser = userService.createUser(new User("personalcode", "newusername", "firstname", "lastname", null));
		Team team = teamService.addUserToTeam(newUser.getId());

		assertEquals(new Long(2L), team.getId());
	}

	public void clearTeams() throws ServiceException {
		List<Team> teams = teamService.getAllTeams();

		for (Team team : teams) {
			List<User> users = userService.getAllUserFromTeam(team.getId());
			for (User user : users) {
				user.setTeam(null);
				userService.updateUser(user);
			}
		}
	}

	@Test
	public void addUserToTeamShouldCreateNewTeamIfAllTeamsAreFull() throws ServiceException {

		clearTeams();

		for (int i = 1; i <= teamService.getAllTeams().size(); i++) {
			for (int j = 1; j <= 10; j++) {
				userService.createUser(new User("personalcode", "username " + i + "" + j, "firstname", "lastname",
						teamService.getTeamById(new Long(i))));
			}
		}
		Team newTeam = teamService.addUserToTeam(1L);
		assertEquals(new Long(4L), newTeam.getId());
	}

	@Test
	public void inActivateTeam() {
		Team inActivatedTeam = teamService.inActivateTeam(1L);
		assertEquals(false, inActivatedTeam.getIsActive());
		teamService.activateTeam(inActivatedTeam.getId());
	}

	@Test
	public void activateTeam() {
		Team inActivatedTeam = teamService.activateTeam(3L);
		assertEquals(true, inActivatedTeam.getIsActive());
	}

	@AfterClass
	public static void tearDown() {
		context.close();
	}

}
