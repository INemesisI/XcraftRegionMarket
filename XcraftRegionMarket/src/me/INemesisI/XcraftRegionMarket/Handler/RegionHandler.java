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
			region.getMembers().removePlayer(member);
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

	public Map<String, Integer> getRegionCount(Player player, String type) {
		Map<String, Integer> count = new HashMap<String, Integer>();
		count.put("global", 0);
		for (World world : plugin.getServer().getWorlds()) {
			for (ProtectedRegion region : worldguard.getRegionManager(world).getRegions().values()) {
				if (region.getOwners().getGroups().contains("xrm") && region.getOwners().contains(worldguard.wrapPlayer(player))) {
					if (type.equals("sold")) {
						for (MarketSign ms : plugin.marketHandler.getAllMarketSigns()) {
							if (ms.getRegion().equals(region.getId()) && (ms.getType().equals("sold"))) {
								if (region.getParent() != null) {
									String key = "p:" + region.getParent().getId();
									if (count.get(key) == null) count.put(key, 1);
									else
										count.put(key, count.get(key) + 1);
								}

								String key = "w:" + world.getName();
								if (count.get(key) == null) count.put(key, 1);
								else
									count.put(key, count.get(key) + 1);

								count.put("global", count.get("global") + 1);

							}
						}
					} else if (type.equals("rented")) {
						for (Rent rent : plugin.rentHandler.getRents()) {
							if (rent.getRegion().equals(region.getId())) {
								if (region.getParent() != null) {
									String key = "p:" + region.getParent().getId();
									if (count.get(key) == null) count.put(key, 0);
									else
										count.put(key, count.get(key) + 1);
								}

								String key = "w:" + world.getName();
								if (count.get(key) == null) count.put(key, 1);
								else
									count.put(key, count.get(key) + 1);

								count.put("global", count.get("global") + 1);
							}
						}
					}
				}
			}
		}
		plugin.Debug(player.getName() + "s regioncount: " + count.get("global"));
		return count;
	}

	public boolean canBuy(String player, String world, String type, ProtectedRegion region, Map<String, Integer> count) {
		String id = region.getParent().getId();
		boolean foundparent = false;
		Map<String, Integer> limit = null;
		if (type.equals("sell") || type.equals("sold")) limit = plugin.configHandler.getSelllimit();
		else
			limit = plugin.configHandler.getRentlimit();
		for (String key : limit.keySet()) {
			
			//Global check
			if (key.equals("global")) { 
				if (limit.get("global") != -1 && limit.get("global") <= count.get("global")) {
					plugin.Debug(player + " tried to buy a region but had too many. reason: global limit(" + limit.get("global") + ") count: " + count
							.get("global"));
					return false;
				}
			}
			
			//World check
			if (key.equals("w:" + world)) { 
				if (limit.get("w:" + world) != -1 && limit.get("w:" + world) <= count.get("w:" + world)) {
					plugin.Debug(player + " tried to buy a region but had too many. reason: world limit " + world + "(" + limit
							.get("w:" + world) + ") count: " + count.get("w:" + world));
					return false;
				}
			}
			
			//Parent check
			if (key.startsWith("p:") && key.contains(id)) { 
				foundparent = true;
				key = key.replace("p:", "");
				int pcount = 0;
				if (key.contains(", ")) {
					for (String rkey : key.split(", ")) {
						if (count.get("p:" + rkey) != null) pcount += count.get("p:" + rkey);
					}
				} else
					pcount = count.get("p:" + key);
				if (limit.get("p:" + key) <= pcount) {
					plugin.Debug(player + " tried to buy a region but had too many. reason: parent limit " + key + "(" + limit.get(key) + ") count: " + pcount);
					return false;
				}
			}
		}
		
		//Group check
		int glimit = -1;
		if (limit.containsKey("g:default")) glimit = limit.get("g:default");
		
		for (String group : plugin.groupHandler.getPlayerGroups(player)) {
			if (limit.containsKey("g:" + group)) {
				glimit = -1;
				if (limit.get("g:" + group) <= count.get("global")) {
					plugin.Debug(player + " tried to buy a region but had too many. reason: group limit " + group + "(" + limit
							.get("g:" + group) + ") count: " + count.get("global"));
					return false;
				}
			}
		}
		
		//Group default check
		if (glimit != -1 && glimit <= count.get("global")) {
			plugin.Debug(player + " tried to buy a region but had too many. reason: default group limit " + "(" + glimit + ") count: " + count
					.get("global"));
			return false;
		}
		
		//Parent default check
		if (!foundparent && limit.containsKey("p:default") && limit.get("p:default") <= count.get("p:" + id)) {
			plugin.Debug(player + " tried to buy a region but had too many. reason: default parent limit " + "(" + limit.get("p:default") + ") count: " + count
					.get("p:" + id));
			return false;
		}
		
		//finished!
		plugin.Debug("permitted " + player + " to buy region " + region.getId());
		return true;
	}
}