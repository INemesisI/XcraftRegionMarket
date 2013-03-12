package me.INemesisI.XcraftRegionMarket.Handler;

import java.util.HashMap;
import java.util.Map;

import me.INemesisI.XcraftRegionMarket.MarketSign.Type;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.databases.ProtectionDatabaseException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionHandler {
	XcraftRegionMarket plugin = null;
	WorldGuardPlugin worldguard = null;

	public RegionHandler(XcraftRegionMarket instance) {
		plugin = instance;
		worldguard = plugin.getWorldguard();
	}

	public ProtectedRegion getRegion(World world, String region) {
		return worldguard.getRegionManager(world).getRegion(region);
	}

	public void removeAllPlayers(ProtectedRegion region) {
		region.getOwners().getPlayers().clear();
		region.getMembers().getPlayers().clear();
	}

	public void setPlayer(ProtectedRegion region, String player) {
		this.removeAllPlayers(region);
		this.addOwner(region, player);
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

	public void addXRM(ProtectedRegion region, Type type) {
		if ((type == Type.SELLING) || (type == Type.SOLD)) {
			region.getOwners().removeGroup("xrm-sell");
		} else {
			region.getOwners().removeGroup("xrm-rent");
		}
	}

	public void removeXRM(ProtectedRegion region, Type type) {
		if ((type == Type.SELLING) || (type == Type.SOLD)) {
			region.getOwners().addGroup("xrm-sell");
		} else {
			region.getOwners().addGroup("xrm-rent");
		}
	}

	public boolean saveRegion(World world) { // TODO: Ability to save only one
												// Region?!
		try {
			worldguard.getRegionManager(world).save();
		} catch (ProtectionDatabaseException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private Map<String, Integer> getRegionCount(HashMap<String, Integer> count, String player, World world, Type type) {
		if (count.get("global") == null) {
			count.put("global", 0);
		}
		for (ProtectedRegion region : worldguard.getRegionManager(world).getRegions().values()) {
			if (region.getOwners().contains(player)) {
				if ((type == Type.SELLING) && region.getOwners().getGroups().contains("xrm-sell")) {
					if (region.getParent() != null) {
						String key = "p:" + region.getParent().getId();
						if (count.get(key) == null) {
							count.put(key, 1);
						} else {
							count.put(key, count.get(key) + 1);
						}
					}
					String key = "w:" + world.getName();
					if (count.get(key) == null) {
						count.put(key, 1);
					} else {
						count.put(key, count.get(key) + 1);
					}

					count.put("global", count.get("global") + 1);

				} else if ((type == Type.RENTING) && region.getOwners().getGroups().contains("xrm-rent")) {
					if (region.getParent() != null) {
						String key = "p:" + region.getParent().getId();
						if (count.get(key) == null) {
							count.put(key, 0);
						} else {
							count.put(key, count.get(key) + 1);
						}
					}

					String key = "w:" + world.getName();
					if (count.get(key) == null) {
						count.put(key, 1);
					} else {
						count.put(key, count.get(key) + 1);
					}

					count.put("global", count.get("global") + 1);
				}
			}
		}

		plugin.Debug(player + "'s regioncount: " + count.get("global"));
		return count;
	}

	public boolean canBuy(Player player, Type type, ProtectedRegion region) {
		Map<String, Integer> limit = null;
		if ((type == Type.SELLING) || (type == Type.SOLD)) {
			limit = plugin.configHandler.getSelllimit();
		} else {
			limit = plugin.configHandler.getRentlimit();
		}

		Map<String, Integer> count = this.getRegionCount(new HashMap<String, Integer>(), player.getName(),
				player.getWorld(), type);

		String group = null;
		int grouplimit = -2;

		String world = player.getWorld().getName();
		int worldlimit = -2;

		String parent = region.getParent().getId();
		int parentlimit = -2;

		for (String key : limit.keySet()) {
			if (key.startsWith("w:") && key.contains(world)) {
				worldlimit = limit.get(key);
			}
			if (key.startsWith("g:")) {
				for (String playergrp : plugin.getPermission().getPlayerGroups(player)) {
					if (key.contains(playergrp) && ((limit.get(key) < grouplimit) || (grouplimit < 0))) {
						grouplimit = limit.get(key);
						group = playergrp;
					}
				}
			}
			if (key.startsWith("p:") && key.contains(parent)) {
				parentlimit = limit.get(key);
			}
		}

		// Group check
		if ((grouplimit >= 0) && (grouplimit <= count.get("global"))) {
			plugin.Debug(player + " tried to buy a region but had too many. reason: group limit \"" + group + "\"("
					+ grouplimit + ") count: " + count.get("g:" + group));
			return false;
		} else { // Default group check
			if (limit.get("g:default") == null) {
				grouplimit = -1;
			} else {
				grouplimit = limit.get("g:default");
			}
			if ((grouplimit >= 0) && (grouplimit <= count.get("global"))) {
				plugin.Debug(player + " tried to buy a region but had too many. reason: default group limit ("
						+ grouplimit + ") count: " + count.get("g:" + group));
				return false;
			}
		}

		// World check
		if ((worldlimit >= 0) && (worldlimit <= count.get("w:" + world))) {
			plugin.Debug(player + " tried to buy a region but had too many. reason: world limit \"" + world + "\"("
					+ worldlimit + ") count: " + count.get("w:" + world));
			return false;
		} else { // Default world check
			if (limit.get("w:default") == null) {
				worldlimit = -1;
			} else {
				worldlimit = limit.get("w:default");
			}
			if ((worldlimit >= 0) && (worldlimit <= count.get("w:" + world))) {
				plugin.Debug(player + " tried to buy a region but had too many. reason: default world limit ("
						+ worldlimit + ") count: " + count.get("w:" + world));
				return false;
			}
		}

		// Parent check
		if ((parentlimit >= 0) && (parentlimit <= count.get("p:" + parent))) {
			plugin.Debug(player + " tried to buy a region but had too many. reason: parent limit \"" + parent + "\"("
					+ parentlimit + ") count: " + count.get("p:" + parent));
			return false;
		} else { // Default parent check
			if (limit.get("p:default") == null) {
				parentlimit = -1;
			} else {
				parentlimit = limit.get("p:default");
			}
			if ((parentlimit >= 0) && (parentlimit <= count.get("p:" + parent))) {
				plugin.Debug(player + " tried to buy a region but had too many. reason: default parent limit ("
						+ parentlimit + ") count: " + count.get("p:" + parent));
				return false;
			}
		}

		// finished!
		plugin.Debug("permitted " + player + " to buy region " + region.getId());
		return true;
	}
}