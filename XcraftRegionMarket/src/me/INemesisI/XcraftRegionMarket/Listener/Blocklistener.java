package me.INemesisI.XcraftRegionMarket.Listener;

import java.util.ArrayList;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.SignChangeEvent;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Blocklistener extends BlockListener {
	XcraftRegionMarket plugin;

	public Blocklistener(XcraftRegionMarket instance) {
		plugin = instance;
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if (!(event.getBlock().getState() instanceof Sign))
			return;
		MarketSign ms = plugin.marketHandler.getMarketSign(event.getBlock());
		if (ms == null)
			ms = plugin.rentHandler.getRent(event.getBlock());
		if (ms == null)
			return;
		if (((ms.getOwner().equals(event.getPlayer().getName()) && event
				.getPlayer().hasPermission("XcraftRegionMarket.Delete")) || event
				.getPlayer().hasPermission("XcraftRegionMarket.Delete.Other"))) {
			if (event.getPlayer().hasPermission("XcraftRegionMarket.Delete.Other")&& !ms.getOwner().equals(event.getPlayer().getName()))
				plugin.getEconomy().depositPlayer(ms.getOwner(), ms.getPrice());
			ProtectedRegion region = plugin.regionHandler.getRegion(ms);
			plugin.regionHandler.removeGroup(region, "xrm");
			plugin.regionHandler.removeAllPlayers(region);
			plugin.regionHandler.saveRegion(event.getBlock().getWorld());
			plugin.marketHandler.remove(ms);
			event.getPlayer().sendMessage(plugin.getName() + "RegionMarket gelöscht!");
		} else {
			event.getPlayer().sendMessage(plugin.getName()+ ChatColor.RED+ "ERROR: Du hast keine Rechte MarketSigns zu löschen!");
			event.setCancelled(true);
		}
	}

	@Override
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		ProtectedRegion region = null;

		if (!event.getLine(0).equals("[sell]")
				&& !event.getLine(0).equals("[rent]")) {
			return;
		}

		// Region check
		Block block = event.getBlock();
		RegionManager mgr = plugin.getWorldguard().getRegionManager(
				block.getWorld());
		region = mgr.getRegion(event.getLine(1));
		if (region == null) {
			player.sendMessage(plugin.getName() + ChatColor.RED
					+ "ERROR: Es konnte keine Region unter der ID "
					+ event.getLine(1) + " gefunden werden.");
			event.setCancelled(true);
			return;
		}
		if (!region.getOwners().getPlayers().contains(player.getName())
				&& !player.hasPermission("XcraftRegionMarket.Sell.All")) {
			player.sendMessage(plugin.getName() + ChatColor.RED
					+ "ERROR: Du hast keine Rechte für diese Region");
		}
		if (!player.hasPermission("XcraftRegionMarket.Create")) {
			player.sendMessage(plugin.getName() + ChatColor.RED
					+ "ERROR: Du hast keine Rechte MarketSigns zu erstellen!");
			return;
		}
		for (MarketSign sign : plugin.marketHandler.getMarketSigns()) {
			if (sign.getRegion().equals(region.getId())) {
				player.sendMessage(plugin.getName()
						+ ChatColor.RED
						+ "ERROR: Es gibt bereits ein MarketSign für diese Region");
				event.setCancelled(true);
				return;
			}
		}
		// Player check
		String playername = event.getLine(3);
		if (playername.isEmpty()
				&& player.hasPermission("XcraftRegionMarket.Sell.All"))
			playername = player.getName();

		// Price check
		int price = 0;
		String id = event.getLine(2).split(":")[0];
		Globalprice gp = null;
		if (!id.matches("\\d*")) {
			gp = plugin.marketHandler.getGlobalPrice(id);
			if (gp == null
					|| !player.hasPermission("XcraftRegionMarket.GP.Use")) {
				event.getPlayer().sendMessage(
						plugin.getName() + ChatColor.RED
								+ "ERROR: Unbekannter Preis");
				event.setCancelled(true);
				return;
			} else
				price = gp.getPrice();
		} else
			price = Integer.parseInt(id);

		// Create the MarketSign
		MarketSign ms = null;
		if (event.getLine(0).equals("[sell]")) {
			ms = new MarketSign(event.getBlock(), region.getId(), "sell", playername, price);
		}
		if (event.getLine(0).equals("[rent]")) {
			String intervall = event.getLine(2).split(":")[1];
			int day = 0;
			int hour = 0;
			for (String t : intervall.split(";")) {
				if (t.contains("w") || t.contains("week")) {
					int i = t.indexOf("w");
					day = Integer.parseInt(t.substring(0, i).trim()) * 7;
				} else if (t.contains("d") || t.contains("day")) {
					int i = t.indexOf("d");
					day = Integer.parseInt(t.substring(0, i).trim());
				} else if (t.contains("h") || t.contains("hour")) {
					int i = t.indexOf("h");
					hour = Integer.parseInt(t.substring(0, i).trim());
				}
			}
			if (day > 1)
				intervall = day + " Tage & ";
			else
				intervall = day + " Tag & ";
			intervall = intervall + hour + " Std.";
			ms = new MarketSign(event.getBlock(), region.getId(), "rent", playername, price, intervall);
		}
		if (gp != null) {
			gp.addSign(ms);
			ms.setPrice(gp.getPrice(region));
		}
		plugin.marketHandler.add(ms);
		ArrayList<String> lines = plugin.marketHandler.update(ms);
		for (int i = 0; i < 4; i++) {
			event.setLine(i, lines.get(i));
		}
		for (int i = 0; i < 4; i++) {
			event.setLine(i, event.getLine(i));
		}
		plugin.regionHandler.removeAllPlayers(region);
		plugin.regionHandler.addGroup(region, "xrm");
		player.sendMessage(plugin.getName() + "RegionMarket wurde erstellt!");
	}

}