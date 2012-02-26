package me.INemesisI.XcraftRegionMarket.Handler;

import java.util.ArrayList;
import java.util.List;

import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

public class GroupHandler {
	XcraftRegionMarket plugin;
	PermissionsPlugin permission;

	public GroupHandler(XcraftRegionMarket instance) {
		plugin = instance;
		permission = plugin.getPermission();
	}

	public String setPermGroup(String player, String type, String region) {
		List<String> pgroups = getPlayerGroups(player);
		for (String g : plugin.configHandler.getIgnoredgroups()) {
			if (pgroups.contains(g)) {
				plugin.Debug(player + " was in ignored group " + g + ", no group changes");
				return null;
			}
		}
		String group = null;
		if (type.equals("sell") || type.equals("sold")) {
			for (String groupkey : plugin.configHandler.getSellgroups().keySet()) {
				if (pgroups.contains(groupkey)) {
					ArrayList<String> regions = plugin.configHandler.getSellgroups().get(groupkey);
					if (regions.contains(region)) group = groupkey;
				}
			}
		} else {
			for (String groupkey : plugin.configHandler.getRentgroups().keySet()) {
				ArrayList<String> regions = plugin.configHandler.getRentgroups().get(groupkey);
				if (regions.contains(region) && pgroups.contains(groupkey)) group = groupkey;
			}
		}
		if (group != null) setPlayersGroup(player, group);
		return group;
	}

	public String getPlayerGroup(String player) {
		return permission.getGroups(player).get(0).getName();
	}

	public List<String> getPlayerGroups(String player) {
		List<String> groups = new ArrayList<String>();
		for (Group g : permission.getGroups(player)) {
			groups.add(g.getName());
		}
		plugin.Debug("Player " + player + "s groups: " + groups.toString());
		return groups;
	}

	public boolean addPlayerToGroup(String player, String group) {
		plugin.Debug("Group" + group + " was added to Player" + player);
		return plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions player addgroup " + player + " " + group);
	}

	public boolean setPlayersGroup(String player, String group) {
		plugin.Debug("Group" + group + " was set to Player" + player);
		return plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions player setgroup " + player + " " + group);
	}

	public boolean removePlayerFromGroup(String player, String group) {
		plugin.Debug("Group" + group + " was removed from Player" + player);
		return plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions player removegroup " + player + " " + group);
	}

}
