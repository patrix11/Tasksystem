package se.group.tasksystem.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import se.group.tasksystem.data.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

	@Query(value = "SELECT u FROM User u WHERE u.username LIKE %?1 AND u.firstname LIKE %?2 AND u.lastname LIKE %?3")
	List<User> getUsersBy(String username, String firstname, String lastname);

	List<User> findByTeamId(Long teamId);
	
	User findByPersonalCode(String personalCode);
	
	User findByUsername(String username);

}
