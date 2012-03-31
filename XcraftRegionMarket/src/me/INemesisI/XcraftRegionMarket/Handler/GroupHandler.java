package me.INemesisI.XcraftRegionMarket.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import net.milkbowl.vault.permission.Permission;

import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

public class GroupHandler {
	XcraftRegionMarket plugin;
	Permission permission;

	public GroupHandler(XcraftRegionMarket instance) {
		plugin = instance;
		permission = plugin.getPermission();
	}

	public String setPermGroup(Player player, String type, String parent) {
		List<String> pgroups = Arrays.asList(permission.getPlayerGroups(player));
		plugin.Debug("Player " + player + "s groups: " + pgroups.toString());
		for (String g : plugin.configHandler.getIgnoredgroups()) {
			for (String pgroup : pgroups) {
				if (g.equalsIgnoreCase(pgroup)) {
					plugin.Debug(player + " was in ignored group " + g + ", no group changes");
					return null;
				}
			}
		}
		String group = null;
		if (type.equals("sell") || type.equals("sold")) {

			for (String groupkey : plugin.configHandler.getSellgroups().keySet()) {
				if (pgroups.contains(groupkey)) continue;
				ArrayList<String> regions = plugin.configHandler.getSellgroups().get(groupkey);
				if (regions.contains(parent)) group = groupkey;
			}
		} else {
			for (String groupkey : plugin.configHandler.getRentgroups().keySet()) {
				if (pgroups.contains(groupkey)) continue;
				ArrayList<String> regions = plugin.configHandler.getRentgroups().get(groupkey);
				if (regions.contains(parent)) group = groupkey;
			}
			permission.playerAddGroup(player, group);
			plugin.Debug("Group" + group + " was added to Player" + player);
		}
		if (group != null) {
			permission.playerAddGroup(player, group);
			plugin.Debug("Group" + group + " was added to Player" + player);
			return group;
		}
		return null;
	}
}
