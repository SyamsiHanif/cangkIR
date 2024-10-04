package model;

public class Transaction {
	private String transactionID;
	private String userID;
	private String courierID;
	private String transactionDate;
	private boolean userDeliveryInsurance;
	
	public Transaction(String transactionID, String userID, String courierID, String transactionDate,
			boolean userDeliveryInsurance) {
		super();
		this.transactionID = transactionID;
		this.userID = userID;
		this.courierID = courierID;
		this.transactionDate = transactionDate;
		this.userDeliveryInsurance = userDeliveryInsurance;
	}
	
	public String getTransactionID() {
		return transactionID;
	}
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getCourierID() {
		return courierID;
	}
	public void setCourierID(String courierID) {
		this.courierID = courierID;
	}
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public boolean isUserDeliveryInsurance() {
		return userDeliveryInsurance;
	}
	public void setUserDeliveryInsurance(boolean userDeliveryInsurance) {
		this.userDeliveryInsurance = userDeliveryInsurance;
	}
	
}
