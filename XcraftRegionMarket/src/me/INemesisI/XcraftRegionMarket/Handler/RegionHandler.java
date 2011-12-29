package me.INemesisI.XcraftRegionMarket.Handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.Rent;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.platymuus.bukkit.permissions.PermissionsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionHandler {
	XcraftRegionMarket plugin = null;
	PermissionsPlugin permission = null;
	WorldGuardPlugin worldguard = null;

	public RegionHandler(XcraftRegionMarket instance) {
		plugin = instance;
		permission = plugin.getPermission();
		worldguard = plugin.getWorldguard();
	}

	public ProtectedRegion getRegion(MarketSign ms) {
		return worldguard.getRegionManager(ms.getBlock().getWorld()).getRegion(ms.getRegion());
	}

	public void removeAllPlayers(ProtectedRegion region) {
		for (String owner : region.getOwners().getPlayers())
			region.getOwners().removePlayer(owner);
		for (String member : region.getMembers().getPlayers())
			region.getOwners().removePlayer(member);
	}

	public void setPlayer(ProtectedRegion region, String player) {
		removeAllPlayers(region);
		addOwner(region, player);
	}

	public void addOwner(ProtectedRegion region, String player) {
		region.getOwners().addPlayer(player);
	}

	public void removeOwner(ProtectedRegion region, String player) {
		region.getOwners().removePlayer(player);
	}

	public void addMember(ProtectedRegion region, String player) {
		region.getMembers().addPlayer(player);
	}

	public void removeMember(ProtectedRegion region, String player) {
		region.getMembers().removePlayer(player);
	}

	public void addGroup(ProtectedRegion region, String group) {
		region.getOwners().addGroup(group);
	}

	public void removeGroup(ProtectedRegion region, String group) {
		region.getOwners().removeGroup(group);
	}

	public boolean saveRegion(World world) {
		try {
			worldguard.getRegionManager(world).save();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public Map<String, Integer> getRegionCount(Player player, World world, String type) {
		Map<String, Integer> count = new HashMap<String, Integer>();
		count.put("global", 0);
		for (ProtectedRegion region : worldguard.getRegionManager(world).getRegions().values()) {
			if (region.getOwners().getGroups().contains("xrm") && region.getOwners().contains(worldguard.wrapPlayer(player))) {
				if (!type.equals("rented") && !type.equals("rent")) {
					for (MarketSign ms : plugin.marketHandler.getAllMarketSigns()) {
						if (ms.getRegion().equals(region.getId()) && (ms.getType().equals("sold"))) {
							count.put("global", count.get("global") + 1);
							if (region.getParent() != null) {
								String key = "p:" + region.getParent().getId();
								if (count.get(key) == null) count.put(key, 1);
								else
									count.put(key, count.get(key) + 1);
							}
						}
					}
				} else {
					for (Rent rent : plugin.rentHandler.getRents()) {
						if (rent.getRegion().equals(region.getId())) {
							count.put("global", count.get("global") + 1);
							String key = "p:" + region.getParent().getId();
							if (count.get(key) == null) count.put(key, 0);
							else
								count.put(key, count.get(key) + 1);
						}
					}
				}
			}
		}
		plugin.Debug(player.getName() + "s regioncount: " + count.get("global"));
		return count;
	}

	public boolean canBuy(String player, String world, String type, ProtectedRegion region, Map<String, Integer> count) {
		Map<String, Integer> limit = null;
		if (type.equals("sell") || type.equals("sold")) limit = plugin.configHandler.getSelllimit();
		else
			limit = plugin.configHandler.getRentlimit();
		if (limit.get("global") != -1 && limit.get("global") <= count.get("global")) {
			plugin.Debug(player + " tried to buy a region but had too many. reason: global limit(" + limit.get("global") + ") count: " + count.get("global"));
			return false;
		}
		String key = "w:" + world;
		if (limit.containsKey(key)) if (limit.get(key) != -1 && limit.get(key) <= count.get("global")) {
			plugin.Debug(player + " tried to buy a region but had too many. reason: world limit " + key + "(" + limit.get(key) + ") count: " + count.get("global"));
			return false;
		}
		key = "g:" + plugin.groupHandler.getPlayerGroup(player);
		if (limit.containsKey(key)) if (limit.get(key) != -1 && limit.get(key) <= count.get("global")) {
			plugin.Debug(player + " tried to buy a region but had too many. reason: group limit " + key + "(" + limit.get(key) + ") count: " + count.get("global"));
			return false;
		}
		if (region.getParent() != null) {
			key = "p:" + region.getParent().getId();
			if (limit.containsKey(key) && count.containsKey(key)) if (limit.get(key) != -1 && limit.get(key) <= count.get(key)) {
				plugin.Debug(player + " tried to buy a region but had too many. reason: parent limit " + key + "(" + limit.get(key) + ") count: " + count.get(key));
				return false;
			}
		}
		plugin.Debug("permitted " + player + " to buy the region " + region.getId() + " count: " + count);
		return true;
	}
}