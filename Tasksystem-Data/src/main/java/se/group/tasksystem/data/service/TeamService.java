package se.group.tasksystem.data.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;

import se.group.tasksystem.data.exception.ServiceException;
import se.group.tasksystem.data.model.Team;
import se.group.tasksystem.data.model.User;
import se.group.tasksystem.data.repository.TeamRepository;
import se.group.tasksystem.data.repository.UserRepository;

@Service
public class TeamService {

	private final TeamRepository teamRepository;
	private final UserRepository userRepository;

	@Autowired
	public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
		this.teamRepository = teamRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public Team createTeam(Team team) throws ServiceException {

		try {
			return teamRepository.save(team);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Transactional
	public Team updateTeam(Team team) throws ServiceException {

		if (!teamRepository.exists(team.getId())) {
			throw new ServiceException("Team with id: " + team.getId() + " doesn't exist");
		}

		try {
			Team currentTeam = teamRepository.findOne(team.getId());
			currentTeam.setName(team.getName());
			return teamRepository.save(currentTeam);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	public Team getTeamById(Long id) throws ServiceException {

		if (!teamRepository.exists(id)) {
			throw new ServiceException("Team with id: " + id + " doesn't exist");
		}

		try {
			return teamRepository.findOne(id);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	public List<Team> getAllTeams() throws ServiceException {

		try {
			return teamRepository.findAll();

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Transactional
	public Team addUserToTeam(Long userId) throws ServiceException {

		if (!userRepository.exists(userId)) {
			throw new ServiceException("User with id: " + userId + " doesn't exist");
		}

		if (!userRepository.findOne(userId).getIsActive()) {
			throw new ServiceException("Could not add user to team since it's inactive");
		}

		try {
			User user = userRepository.findOne(userId);

			for (Team team : teamRepository.findAll()) {
				if (userRepository.findByTeamId(team.getId()).size() < 10 && team.getIsActive()) {
					user.setTeam(team);
					userRepository.save(user);
					return team;
				}
			}
			Team newTeam = teamRepository.save(new Team("Team " + teamRepository.findAll().size() + 1));
			user.setTeam(newTeam);
			return newTeam;

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	@Transactional
	public Team inActivateTeam(Long id) throws ServiceException {

		if (!teamRepository.exists(id)) {
			throw new ServiceException("Team with id: " + id + " doesn't exist");
		}

		try {
			Team team = teamRepository.findOne(id);
			team.setIsActive(false);
			return teamRepository.save(team);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	@Transactional
	public Team activateTeam(Long id) throws ServiceException {

		if (!teamRepository.exists(id)) {
			throw new ServiceException("Team with id: " + id + " doesn't exist");
		}

		try {
			Team team = teamRepository.findOne(id);
			team.setIsActive(true);
			return teamRepository.save(team);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

}
