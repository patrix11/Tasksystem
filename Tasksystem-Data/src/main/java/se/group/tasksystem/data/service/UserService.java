package se.group.tasksystem.data.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;

import se.group.tasksystem.data.exception.ServiceException;
import se.group.tasksystem.data.model.User;
import se.group.tasksystem.data.model.WorkItem;
import se.group.tasksystem.data.model.WorkItem.WorkItemStatus;
import se.group.tasksystem.data.repository.TeamRepository;
import se.group.tasksystem.data.repository.UserRepository;
import se.group.tasksystem.data.repository.WorkItemRepository;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final WorkItemRepository workItemRepository;
	private final TeamRepository teamRepository;

	@Autowired
	public UserService(UserRepository userRepository, WorkItemRepository workItemRepository,
			TeamRepository teamRepository) {
		this.userRepository = userRepository;
		this.workItemRepository = workItemRepository;
		this.teamRepository = teamRepository;
	}

	public Iterable<User> getPaginatedUsers(PageRequest pageRequest) {
		return userRepository.findAll(pageRequest);
	}

	public User createUser(User user) throws ServiceException {
		if (usernameExists(user.getUsername())) {
			throw new ServiceException("User with username: " + user.getUsername() + " already exists");
		}

		if (user.getUsername().length() < 10) {
			throw new ServiceException("Username too short, must be 10 characters");
		}

		try {
			return userRepository.save(user);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Transactional
	public User updateUser(User user) throws ServiceException {

		if (!userRepository.exists(user.getId())) {
			throw new ServiceException("User with id: " + user.getId() + " doesn't exist");
		}

		if (user.getUsername().length() < 10) {
			throw new ServiceException("Username too short, must be 10 characters");
		}

		try {
			User currentUser = userRepository.findOne(user.getId());
			currentUser.setPersonalCode(user.getPersonalCode());
			currentUser.setUsername(user.getUsername());
			currentUser.setFirstname(user.getFirstname());
			currentUser.setLastname(user.getLastname());
			currentUser.setTeam(user.getTeam());

			return userRepository.save(currentUser);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	public User getUserById(Long id) throws ServiceException {

		if (!userRepository.exists(id)) {
			throw new ServiceException("User with id: " + id + " doesn't exist");
		}

		try {
			return userRepository.findOne(id);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	public User getUserByPersonalCode(String personalCode) throws ServiceException {

		try {
			return userRepository.findByPersonalCode(personalCode);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public List<User> getUsersBy(String username, String firstname, String lastname) throws ServiceException {

		try {
			return userRepository.getUsersBy(username, firstname, lastname);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public List<User> getAllUserFromTeam(Long teamId) throws ServiceException {

		if (!teamRepository.exists(teamId)) {
			throw new ServiceException("Team with team id: " + teamId + " doesn't exist");
		}

		try {
			return userRepository.findByTeamId(teamId);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	@Transactional
	public User inActivateUser(Long id) throws ServiceException {

		if (!userRepository.exists(id)) {
			throw new ServiceException("User with id: " + id + " doesn't exist");
		}

		try {
			User user = userRepository.findOne(id);
			user.setIsActive(false);

			List<WorkItem> workItems = workItemRepository.findByUserId(id);

			for (WorkItem workItem : workItems) {
				workItem.setStatus(WorkItemStatus.UNSTARTED);
				workItemRepository.save(workItem);
			}

			return userRepository.save(user);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	@Transactional
	public User activateUser(Long id) throws ServiceException {

		if (!userRepository.exists(id)) {
			throw new ServiceException("User with id: " + id + " doesn't exist");
		}

		try {
			User user = userRepository.findOne(id);
			user.setIsActive(true);
			return userRepository.save(user);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	@Transactional
	public void addWorkItemToUser(Long userId, Long workItemId) throws ServiceException {

		if (!userRepository.exists(userId)) {
			throw new ServiceException("User does not exist");
		}

		if (!userRepository.findOne(userId).getIsActive()) {
			throw new ServiceException("Could not add workItem to user because its inactive");
		}
		if (workItemRepository.findByUserId(userId).size() >= 5) {
			throw new ServiceException("Could not add workItem to user because user already has 5 workItems");
		}

		try {
			WorkItem workItem = workItemRepository.findOne(workItemId);
			workItem.setUser(userRepository.findOne(userId));
			workItemRepository.save(workItem);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	private boolean usernameExists(String username) {

		User user = userRepository.findByUsername(username);
		if (user != null) {
			return true;
		}
		return false;
	}

}
