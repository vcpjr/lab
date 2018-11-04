package pojo;

import java.util.ArrayList;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a Tweet.
 * 
 * TODO create in the next versions a superclass TextualCLip to make a most generic mapping
 *
 * @author Vilmar César Pereira Júnior
 */
public class Tweet {

	@SerializedName("id")
	private Long id;
	
	@SerializedName("userid")
	private Long userId;
	
	@SerializedName("text")
	private String text;
	
	@SerializedName("createdat")
	private Date creationDate;
	
	@SerializedName("isretweet")
	private boolean isRetweet;
	
	private ArrayList<KGNode> annotatedResources;
	
	public Tweet() {
		
	}

	public Tweet(Long id, Long userId, String text, Date creationDate, boolean isRetweet) {
		super();
		this.id = id;
		this.userId = userId;
		this.text = text;
		this.creationDate = creationDate;
		this.isRetweet = isRetweet;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
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

	public ArrayList<KGNode> getAnnotatedResources() {
		return annotatedResources;
	}

	public void setAnnotatedResources(ArrayList<KGNode> annotatedResources) {
		this.annotatedResources = annotatedResources;
	}

	@Override
	public String toString() {
		return "Tweet [id=" + id + ", userId=" + userId + ", text=" + text + ", creationDate=" + creationDate
				+ ", isRetweet=" + isRetweet + ", annotatedResources=" + annotatedResources + "]";
	}
}