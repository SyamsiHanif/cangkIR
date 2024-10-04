package model;

public class Account {
	private String userID;
	private String username;
	private String email;
	private String password;
	private String gender;
	
	public Account(String userID, String username, String email, String password, String gender) {
		super();
		this.userID = userID;
		this.username = username;
		this.email = email;
		this.password = password;
		this.gender = gender;
	}
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
}
