package model;

public class Courier {
	private String courierID;
	private String courierName;
	private Integer courierPrice;
	
	public Courier(String courierID, String courierName, Integer courierPrice) {
		super();
		this.courierID = courierID;
		this.courierName = courierName;
		this.courierPrice = courierPrice;
	}
	
	public String getCourierID() {
		return courierID;
	}
	public void setCourierID(String courierID) {
		this.courierID = courierID;
	}
	public String getCourierName() {
		return courierName;
	}
	public void setCourierName(String courierName) {
		this.courierName = courierName;
	}
	public Integer getCourierPrice() {
		return courierPrice;
	}
	public void setCourierPrice(Integer courierPrice) {
		this.courierPrice = courierPrice;
	}
	
}
