package me.INemesisI.XcraftRegionMarket;

import org.bukkit.block.Block;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public abstract class MarketSign {
	protected Block sign;
	protected ProtectedRegion region;
	protected String owner;
	protected double price;
	protected Globalprice gp;
	protected Type type;

	public MarketSign(Block block, ProtectedRegion region, Type type, String owner, double price) {
		this.setBlock(block);
		this.setRegion(region);
		this.setOwner(owner);
		this.setPrice(price);
		this.setType(type);
	}

	public MarketSign(Block block, ProtectedRegion region, Type type, String owner, double price, Globalprice gp) {
		this.setBlock(block);
		this.setRegion(region);
		this.setOwner(owner);
		this.setPrice(price);
		this.setType(type);
		this.setGp(gp);
	}

	public void updatePrice() {
		if (gp == null) {
			return;
		}
		BlockVector max = region.getMaximumPoint();
		BlockVector min = region.getMinimumPoint();
		max.setY(min.getY());
		int x = 0;
		int z = 0;
		if (max.getX() > min.getX()) {
			x = (int) (max.getX() - min.getX()) + 1;
		} else {
			x = (int) (min.getX() - max.getX()) + 1;
		}
		if (max.getZ() > min.getZ()) {
			z = (int) (max.getZ() - min.getZ()) + 1;
		} else {
			z = (int) (min.getZ() - max.getZ()) + 1;
		}
		price = x * z * gp.getPrice();
	}

	public enum Type {
		SELLING, SOLD, RENTING, RENTED
	}

	public Block getBlock() {
		return sign;
	}

	public void setBlock(Block block) {
		sign = block;
	}

	public ProtectedRegion getRegion() {
		return region;
	}

	public void setRegion(ProtectedRegion region) {
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

	public Globalprice getGp() {
		return gp;
	}

	public void setGp(Globalprice gp) {
		this.gp = gp;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
