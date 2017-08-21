package se.group.tasksystem.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import se.group.tasksystem.data.model.Team;

public interface TeamRepository extends CrudRepository<Team, Long> {

	List<Team> findAll();

}
