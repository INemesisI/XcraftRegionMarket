package me.INemesisI.XcraftRegionMarket.Handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.MarketSign.Type;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.entity.Player;

public class GroupHandler {
	XcraftRegionMarket plugin;
	Permission permission;

	public GroupHandler(XcraftRegionMarket instance) {
		plugin = instance;
		permission = plugin.getPermission();
	}

	public String setPermGroup(Player player, Type type, String regionparent) {
		List<String> pgroups = Arrays.asList(permission.getPlayerGroups(player));
		plugin.Debug("Player " + player + "s groups: " + pgroups.toString());
		for (String ignoredgrp : plugin.configHandler.getIgnoredgroups()) {
			for (String playergrp : pgroups) {
				if (ignoredgrp.equalsIgnoreCase(playergrp)) {
					plugin.Debug(player + " was in ignored group " + ignoredgrp + ", no group changes");
					return null;
				}
			}
		}
		String group = null;
		if (type.equals(MarketSign.Type.SELL) || type.equals(MarketSign.Type.SOLD)) {
			for (String groupkey : plugin.configHandler.getSellgroups().keySet()) {
				if (pgroups.contains(groupkey)) continue;
				ArrayList<String> regions = plugin.configHandler.getSellgroups().get(groupkey);
				if (regions.contains(regionparent)) group = groupkey;
			}
		} else {
			for (String groupkey : plugin.configHandler.getRentgroups().keySet()) {
				if (pgroups.contains(groupkey)) continue;
				ArrayList<String> regions = plugin.configHandler.getRentgroups().get(groupkey);
				if (regions.contains(regionparent)) group = groupkey;
			}
			permission.playerAddGroup(player, group);
			plugin.Debug("Group \"" + group + "\" was added to Player \"" + player + "\"");
		}
		if (group != null) {
			permission.playerAddGroup(player, group);
			plugin.Debug("Group \"" + group + "\" was added to Player \"" + player + "\"");
			return group;
		}
		return null;
	}
}
