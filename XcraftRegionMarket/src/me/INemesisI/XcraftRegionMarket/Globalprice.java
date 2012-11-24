package me.INemesisI.XcraftRegionMarket;

public class Globalprice {
	private String ID;
	private int price;

	public Globalprice(String ID, int price) {
		this.ID = ID;
		this.price = price;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return ID + ":" + price;
	}

}
