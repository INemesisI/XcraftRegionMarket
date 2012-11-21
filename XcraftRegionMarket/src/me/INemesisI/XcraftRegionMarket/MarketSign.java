package me.INemesisI.XcraftRegionMarket;

import org.bukkit.block.Block;

public class MarketSign {
	private Block sign;
	private String region;
	private String owner;
	private double price;
	private Type type;
	private String intervall;

	public MarketSign(Block block, String region, Type type, String owner, double price) {
		this.setBlock(block);
		this.setRegion(region);
		this.setOwner(owner);
		this.setPrice(price);
		this.setType(type);
		this.setIntervall("");
	}

	public MarketSign(Block block, String region, Type type, String owner, double price, String intervall) {
		this.setBlock(block);
		this.setRegion(region);
		this.setOwner(owner);
		this.setPrice(price);
		this.setType(type);
		this.setIntervall(intervall);
	}

	public enum Type {
		SELL, SOLD, RENT, RENTED
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getIntervall() {
		return intervall;
	}

	public void setIntervall(String intervall) {
		this.intervall = intervall;
	}
}
