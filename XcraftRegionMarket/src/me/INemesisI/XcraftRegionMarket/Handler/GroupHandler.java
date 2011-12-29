package me.INemesisI.XcraftRegionMarket.Handler;

import java.util.ArrayList;
import java.util.List;

import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import com.platymuus.bukkit.permissions.Group;
import com.platymuus.bukkit.permissions.PermissionsPlugin;

public class GroupHandler{
	XcraftRegionMarket plugin;
	PermissionsPlugin permission;
	
	public GroupHandler(XcraftRegionMarket instance) {
		plugin = instance;
		permission = plugin.getPermission();
	}
	
	public String setPermGroup(String player, String type, int count) {
		if (type.equals("sell") || type.equals("sold")) {
			String sellgroup = plugin.configHandler.getSellgroups().get(count + 1);
			if (sellgroup != null) {
				for (String g : plugin.configHandler.getIgnoredgroups()) {
					if (getPlayerGroups(player).contains(g)) {
						plugin.Debug(player + " was in ignored group " + g + ", no group changes");
						return null;
					}
				}
			} else {
				plugin.Debug("Info: There is no sellgroup for count " + count + 1);
				return null;
			}
			 setPlayersGroup(player, sellgroup);
			return sellgroup;
		} else {
			String rentgroup = plugin.configHandler.getRentgroups().get(count + 1);
			if (rentgroup != null) {
				plugin.Debug(player + " added to group " + rentgroup);
				addPlayerToGroup(player, rentgroup);
			} else {
				plugin.Debug("Info: There is no rentgroup for count " + count + 1);
				return null;
			}
			return rentgroup;
		}
	}
	
	public String getPlayerGroup(String player) {
		return permission.getGroups(player).get(0).getName();
	}
	
	public List<String> getPlayerGroups(String player) {
		List<String> groups = new ArrayList<String>();
		for (Group g : permission.getGroups(player)) {
			groups.add(g.getName());
		}
		return groups;
	}
	
	public boolean addPlayerToGroup(String player, String group) {
		return plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions player addgroup " + player + " " + group);
	}
	
	public boolean setPlayersGroup(String player, String group) {
		return plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions player setgroup " + player + " " + group);
	}
	
	public boolean removePlayerFromGroup(String player, String group) {
		return plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "permissions player removegroup " + player + " " + group);
	}
	
	

}
