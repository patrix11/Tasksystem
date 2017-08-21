package se.group.tasksystem.data.model;

import javax.persistence.Entity;

@Entity
public class Issue extends AbstractEntity {

	private String title;
	private String description;

	public Issue(String title, String description) {
		this.title = title;
		this.description = description;
	}

	protected Issue() {

	}

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
