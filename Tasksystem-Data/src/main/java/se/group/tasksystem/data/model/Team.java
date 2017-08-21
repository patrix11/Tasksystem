package se.group.tasksystem.data.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Team extends AbstractEntity {

	private String name;

	@OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
	@JsonManagedReference
	private Collection<User> users;

	public Team(String name) {
		this.name = name;
	}

	protected Team() {

	}

	public Collection<User> getUsers() {
		return users;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
