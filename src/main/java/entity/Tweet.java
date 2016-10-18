package entity;

import java.util.List;

public class Tweet {
	// TODO Confirmar tipos
	private Long id;
	private String message;
	private String userName;
	// private List<Mention> mentions;
	private List<String> hashtags;
	private String language; // TODO usar ENUM para o idioma?

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public List<String> getHashtags() {
		return hashtags;
	}

	public void setHashtags(List<String> hashtags) {
		this.hashtags = hashtags;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
