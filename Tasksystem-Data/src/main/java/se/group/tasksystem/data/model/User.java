package se.group.tasksystem.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class User extends AbstractEntity {

	private String personalCode;
	
	@Column(unique = true)
	private String username;

	private String firstname;

	private String lastname;

	@ManyToOne
	@JoinColumn(name = "team_id")
	@JsonBackReference
	private Team team;

	public User(String personalCode, String username, String firstname, String lastname, Team team) {
		this.personalCode = personalCode;
		this.username = username;
		this.firstname = firstname;
		this.lastname = lastname;
		this.team = team;
	}

	protected User() {

	}

	public String getPersonalCode() {
		return personalCode;
	}
	
	public void setPersonalCode(String personalCode) {
		this.personalCode = personalCode;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

}
