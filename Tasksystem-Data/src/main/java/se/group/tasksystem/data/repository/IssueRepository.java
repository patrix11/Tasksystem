package se.group.tasksystem.data.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import se.group.tasksystem.data.model.Issue;

public interface IssueRepository extends PagingAndSortingRepository<Issue, Long> {

}