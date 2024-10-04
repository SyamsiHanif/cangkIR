package model;

public class Cart {
	
	private String userID;
	private String cupID;
	private String cupName;
	private Integer cupPrice;
	private Integer quantity;
	private Integer total;
	
	public Cart(String userID, String cupID, String cupName, Integer cupPrice, Integer quantity, Integer total) {
		super();
		this.userID = userID;
		this.cupID = cupID;
		this.cupName = cupName;
		this.cupPrice = cupPrice;
		this.quantity = quantity;
		this.total = total;
	}
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getCupID() {
		return cupID;
	}
	public void setCupID(String cupID) {
		this.cupID = cupID;
	}
	public String getCupName() {
		return cupName;
	}
	public void setCupName(String cupName) {
		this.cupName = cupName;
	}
	public Integer getCupPrice() {
		return cupPrice;
	}
	public void setCupPrice(Integer cupPrice) {
		this.cupPrice = cupPrice;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getTotal() {
		return total;
	}
	public void setTotal(Integer total) {
		this.total = total;
	}
	
	
}
