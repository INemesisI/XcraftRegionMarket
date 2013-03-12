package me.INemesisI.XcraftRegionMarket;

import org.bukkit.block.Block;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class SellSign extends MarketSign {

	public SellSign(Block block, ProtectedRegion region, Type type, String owner, double price) {
		super(block, region, type, owner, price);
	}

	public SellSign(Block block, ProtectedRegion region, Type type, String owner, double price, Globalprice gp) {
		super(block, region, type, owner, price);
	}

	public boolean sellTo(String player) {
		if (type == Type.SOLD) {
			return false;
		}
		this.setOwner(player);
		this.setType(Type.SOLD);
		return true;
	}

}
