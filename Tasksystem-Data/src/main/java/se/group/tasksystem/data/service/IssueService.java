package se.group.tasksystem.data.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.UnexpectedRollbackException;

import se.group.tasksystem.data.exception.ServiceException;
import se.group.tasksystem.data.model.Issue;
import se.group.tasksystem.data.model.WorkItem;
import se.group.tasksystem.data.model.WorkItem.WorkItemStatus;
import se.group.tasksystem.data.repository.IssueRepository;
import se.group.tasksystem.data.repository.WorkItemRepository;

@Service
public class IssueService {

	private final IssueRepository issueRepository;
	private final WorkItemRepository workItemRepository;

	@Autowired
	public IssueService(IssueRepository issueRepository, WorkItemRepository workItemRepository) {
		this.issueRepository = issueRepository;
		this.workItemRepository = workItemRepository;
	}
	
	public Iterable<Issue> getPaginatedIssues(PageRequest pageRequest) {
		return issueRepository.findAll(pageRequest);
	}


	@Transactional
	public Issue createIssueAndAddToWorkItem(Issue issue, Long workItemId) throws ServiceException {

		if (!workItemRepository.exists(workItemId)) {
			throw new ServiceException("WorkItem with id: " + workItemId + " doesn't exist");
		}

		if (workItemRepository.findOne(workItemId).getStatus() != WorkItemStatus.DONE) {
			throw new ServiceException("Cannot add issue to workItem because workItem is not status DONE");
		}

		try {
			WorkItem workItem = workItemRepository.findOne(workItemId);
			workItem.setIssue(issue);
			workItem.setStatus(WorkItemStatus.UNSTARTED);
			workItemRepository.save(workItem);
			return issueRepository.save(issue);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	@Transactional
	public Issue updateIssue(Issue issue) throws ServiceException {

		if (!issueRepository.exists(issue.getId())) {
			throw new ServiceException("Issue with id: " + issue.getId() + " doesn't exist");
		}

		try {
			Issue currentIssue = issueRepository.findOne(issue.getId());
			currentIssue.setTitle(issue.getTitle());
			currentIssue.setDescription(issue.getDescription());
			return issueRepository.save(currentIssue);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		} catch (UnexpectedRollbackException e) {
			throw new ServiceException(e.getMessage());
		}

	}

	public Issue getIssueById(Long id) throws ServiceException {

		if (!issueRepository.exists(id)) {
			throw new ServiceException("Issue with id: " + id + " doesn't exist");
		}

		try {
			return issueRepository.findOne(id);

		} catch (DataAccessException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public List<Issue> getAllIssue() {
		return (List<Issue>) issueRepository.findAll();

	}

}