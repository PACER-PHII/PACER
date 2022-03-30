package gatech.edu.JobManagementSystem.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class JsonTest{
	private int userId;
	@Id
	private int id;
	private String title;
	private String completed;
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCompleted() {
		return completed;
	}
	public void setCompleted(String completed) {
		this.completed = completed;
	}
	
	@Override
	public String toString() {
		return "JsonTest [userId=" + userId + ", id=" + id + ", title=" + title
				+ ", completed=" + completed + "]";
	}
}
