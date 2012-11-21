package me.INemesisI.XcraftRegionMarket;

import java.util.Date;

import org.bukkit.block.Block;

public class Rent extends MarketSign {
	private Date paytime;
	private String renter;

	public Rent(Block block, String region, String account, double price, String intervall) {
		super(block, region, Type.RENTED, account, price, intervall);
	}

	public Date getPaytime() {
		return paytime;
	}

	public void setPaytime(Date paytime) {
		this.paytime = paytime;
	}

	public String getRenter() {
		return renter;
	}

	public void setRenter(String renter) {
		this.renter = renter;
	}
}
