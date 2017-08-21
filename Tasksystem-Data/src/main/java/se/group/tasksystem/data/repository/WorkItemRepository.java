package se.group.tasksystem.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import se.group.tasksystem.data.model.WorkItem;
import se.group.tasksystem.data.model.WorkItem.WorkItemStatus;

public interface WorkItemRepository extends PagingAndSortingRepository<WorkItem, Long> {

	List<WorkItem> findByUpdatedDateBetweenAndStatusEquals(Date fromDate, Date toDate, WorkItemStatus status);

	List<WorkItem> findByStatus(WorkItemStatus status);

	List<WorkItem> findByIssueIdNotNull();

	List<WorkItem> findByDescriptionContaining(String description);

	List<WorkItem> findByUserId(Long userId);

}
