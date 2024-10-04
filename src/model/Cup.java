package model;

public class Cup {
	
	private String cupID;
	private String cupName;
	private Integer cupPrice;
	
	public Cup(String cupID, String cupName, Integer cupPrice) {
		super();
		this.cupID = cupID;
		this.cupName = cupName;
		this.cupPrice = cupPrice;
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
	
}
