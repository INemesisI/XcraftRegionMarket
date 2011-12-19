package me.INemesisI.XcraftRegionMarket;

import org.bukkit.block.Block;

public class MarketSign {
	private Block sign;
	private String region;
	private String owner;
	private double price;
	private String type;
	private String intervall;

	public MarketSign(Block block, String region, String type, String owner, double price) {
		this.setBlock(block);
		this.setRegion(region);
		this.setOwner(owner);
		this.setPrice(price);
		this.setType(type);
		this.setIntervall("");
	}

	public MarketSign(Block block, String region, String type, String owner, double price, String intervall) {
		this.setBlock(block);
		this.setRegion(region);
		this.setOwner(owner);
		this.setPrice(price);
		this.setType(type);
		this.setIntervall(intervall);
	}

	public Block getBlock() {
		return sign;
	}

	public void setBlock(Block block) {
		this.sign = block;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIntervall() {
		return intervall;
	}

	public void setIntervall(String intervall) {
		this.intervall = intervall;
	}
}
