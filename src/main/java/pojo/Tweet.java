package pojo;

import java.util.Date;

public class Tweet {

	private int id;
	private int userId;
	private String text;
	private Date creationDate;
	private boolean isRetweet;

	public Tweet(int id, int userId, String text, Date creationDate, boolean isRetweet) {
		super();
		this.id = id;
		this.userId = userId;
		this.text = text;
		this.creationDate = creationDate;
		this.isRetweet = isRetweet;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public boolean isRetweet() {
		return isRetweet;
	}
	public void setRetweet(boolean isRetweet) {
		this.isRetweet = isRetweet;
	}
}