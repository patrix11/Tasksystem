package se.group.tasksystem.data.service;

import java.util.ArrayList;
import java.util.Date;
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
public class WorkItemService {

	private final WorkItemRepository workItemRepository;
	private final UserRepository userRepository;
	private final TeamRepository teamRepository;

	@Autowired
	public WorkItemService(WorkItemRepository workItemRepository, UserRepository userRepository,
			TeamRepository teamRepository) {
		this.workItemRepository = workItemRepository;
		this.userRepository = userRepository;
		this.teamRepository = teamRepository;
	}

	public List<WorkItem> getWorkItemHistory(Date fromDate, Date toDate, WorkItemStatus status) {
		return workItemRepository.findByUpdatedDateBetweenAndStatusEquals(fromDate, toDate, status);
	}

	public Iterable<WorkItem> getPaginatedWorkItems(PageRequest pageRequest) {
		return workItemRepository.findAll(pageRequest);
	}

	@Transactional
	public WorkItem createWorkItem(WorkItem workItem) throws ServiceException {

		try {
			return workItemRepository.save(workItem);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Transactional
	public WorkItem updateWorkItem(WorkItem workItem) throws ServiceException {

		if (!workItemRepository.exists(workItem.getId())) {
			throw new ServiceException("WorkItem with id: " + workItem.getId() + " doesn't exist");
		}

		try {
			WorkItem currentWorkItem = workItemRepository.findOne(workItem.getId());
			currentWorkItem.setTitle(workItem.getTitle());
			currentWorkItem.setDescription(workItem.getDescription());
			return workItemRepository.save(currentWorkItem);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	public WorkItem getWorkItemById(Long id) throws ServiceException {

		try {
			return workItemRepository.findOne(id);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	@Transactional
	public WorkItem changeWorkItemStatus(Long id, WorkItemStatus status) throws ServiceException {

		if (!workItemRepository.exists(id)) {
			throw new ServiceException("WorkItem with id: " + id + " doesn't exist");
		}

		try {
			WorkItem workItem = workItemRepository.findOne(id);
			workItem.setStatus(status);
			workItemRepository.save(workItem);
			return workItem;

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	@Transactional
	public void removeWorkItem(Long id) throws ServiceException {

		if (!workItemRepository.exists(id)) {
			throw new ServiceException("WorkItem with id: " + id + " doesn't exist");
		}
		try {
			workItemRepository.delete(id);
		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	public List<WorkItem> getWorkItemsByStatus(WorkItemStatus status) throws ServiceException {

		try {
			return workItemRepository.findByStatus(status);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public List<WorkItem> getWorkItemsWithIssue() throws ServiceException {

		try {
			return workItemRepository.findByIssueIdNotNull();

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public List<WorkItem> getWorkItemsByDescription(String description) throws ServiceException {

		try {
			return workItemRepository.findByDescriptionContaining(description);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public List<WorkItem> getAllWorkItemsByUser(Long userId) throws ServiceException {

		if (!userRepository.exists(userId)) {
			throw new ServiceException("User with user id: " + userId + " doesn't exist");
		}

		try {
			return workItemRepository.findByUserId(userId);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	public List<WorkItem> getAllWorkitem() {
		return (List<WorkItem>) workItemRepository.findAll();
	}

	public List<WorkItem> getAllWorkItemsByTeam(Long teamId) throws ServiceException {

		if (!teamRepository.exists(teamId)) {
			throw new ServiceException("WorkItem with id: " + teamId + " doesn't exist");
		}

		try {
			List<User> users = userRepository.findByTeamId(teamId);

			List<WorkItem> workItemsFromUsers = new ArrayList<>();
			for (User user : users) {

				List<WorkItem> workItems = workItemRepository.findByUserId(user.getId());
				for (WorkItem workItem : workItems) {
					workItemsFromUsers.add(workItem);
				}
			}
			return workItemsFromUsers;

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}
	}

}
