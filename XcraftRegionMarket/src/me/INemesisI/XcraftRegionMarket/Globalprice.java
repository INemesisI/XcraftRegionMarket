package me.INemesisI.XcraftRegionMarket;

import java.util.ArrayList;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Globalprice {
	String ID;
	int price;
	ArrayList<MarketSign> marketsigns = new ArrayList<MarketSign>();
	
	public Globalprice(String ID, int price) {
		this.ID = ID;
		this.price = price;
	}

	public Globalprice(String ID, int price, ArrayList<MarketSign> marketsigns) {
		this.setID(ID);
		this.setPrice(price);
		this.marketsigns = marketsigns;
	}

	public int getPrice(ProtectedRegion region) {
		BlockVector max = region.getMaximumPoint();
		BlockVector min = region.getMinimumPoint();
		max.setY(min.getY());
		int x = 0;
		int z = 0;
		if (max.getX() > min.getX())
			x = (int) (max.getX() - min.getX()) + 1;
		else
			x = (int) (min.getX() - max.getX()) + 1;
		if (max.getZ() > min.getZ())
			z = (int) (max.getZ() - min.getZ()) + 1;
		else
			z = (int) (min.getZ() - max.getZ()) + 1;
		return (x * z) * price;
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

	public void addSign(MarketSign marketsign) {
		marketsigns.add(marketsign);
	}

	public boolean removeSign(MarketSign marketsign) {
		return marketsigns.remove(marketsign);
	}

	public ArrayList<MarketSign> getMarketSigns() {
		return marketsigns;
	}

	@Override
	public String toString() {
		return ID + ":" + price;
	}

}
