package me.INemesisI.XcraftRegionMarket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class EventListener implements Listener {
	private XcraftRegionMarket plugin;

	public EventListener(XcraftRegionMarket instance) {
		plugin = instance;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getState() instanceof Sign) {
			MarketSign ms = plugin.marketHandler.getMarketSign(event.getClickedBlock());
			if (ms == null) ms = plugin.rentHandler.getRent(event.getClickedBlock());
			if (ms == null) return;

			String type = ms.getType();
			Player player = event.getPlayer();
			if (player.hasPermission("XcraftRegionMarket.Mod")) {
				player.sendMessage(plugin.getCName() + "MarketSign Info:");
				player.sendMessage(plugin.getCName() + "Type: " + ChatColor.GOLD + ms.getType());
				player.sendMessage(plugin.getCName() + "Region: " + ChatColor.GOLD + ms.getRegion());
				String price = plugin.getEconomy().format(ms.getPrice());
				for (Globalprice gp : plugin.marketHandler.getGlobalPrices()) {
					if (!ms.getType().equals("rented")) {
						for (MarketSign gpms : gp.getMarketSigns()) {
							if (gpms.equals(ms)) price += " (GP: " + gp.toString() + ")";
						}
					}
				}
				player.sendMessage(plugin.getCName() + "Price: " + ChatColor.GOLD + price);
				player.sendMessage(plugin.getCName() + "Owner: " + ChatColor.GOLD + ms.getOwner());
				if (ms.getType().equals("rented") || ms.getType().equals("rent")) player
						.sendMessage(plugin.getCName() + "Intervall: " + ChatColor.GOLD + ms.getIntervall());
				Rent rent = plugin.rentHandler.getRent(ms.getBlock());
				if (rent != null) {
					player.sendMessage(plugin.getCName() + "Renter: " + ChatColor.GOLD + rent.getRenter());
					SimpleDateFormat date = new SimpleDateFormat();
					date.applyPattern("yyyy.MM.dd HH:mm");
					player.sendMessage(plugin.getCName() + "Paytime: " + ChatColor.GOLD + date.format(rent.getPaytime()));
				}
				plugin.clicked.put(player.getName(), ms);
				return;
			}
			if (type.equals("sell") && (player.hasPermission("XcraftRegionMarket.Buy")) || (type.equals("rent") && player
					.hasPermission("XcraftRegionMarket.Rent"))) {
				if (type.equals("sell") && ms.getOwner().equals(player.getName())) {
					player.sendMessage(plugin.getCName() + "Möchtest du dieses Grundstück nicht mehr verkaufen?");
					player.sendMessage(plugin.getCName() + "Schreibe /rm stop um den verkauf zu stoppen");
					plugin.clicked.put(player.getName(), ms);
					return;
				}
				if (ms.getType().equals("sell") || ms.getType().equals("rent")) {
					if (ms.getType().equals("sell")) {
						player.sendMessage(plugin.getCName() + "Möchtest du dieses Grundstück kaufen?");
						player.sendMessage(plugin.getCName() + "Preis: " + ChatColor.GOLD + plugin.getEconomy().format(ms.getPrice()));
						player.sendMessage(plugin.getCName() + "Schreibe /rm confirm um es zu kaufen.");
					}
					if (ms.getType().equals("rent")) {
						String intervall = ms.getIntervall();
						if (!intervall.isEmpty()) {
							String[] split = intervall.split(" & ");
							if (split[0].substring(0, 1).equals("0")) intervall = split[1];
							if (split[1].substring(0, 1).equals("0")) intervall = split[0];
						}
						player.sendMessage(plugin.getCName() + "Möchtest du dieses Grundstück mieten?");
						player.sendMessage(plugin.getCName() + "Preis: " + ChatColor.GOLD + plugin.getEconomy().format(ms.getPrice()) + " alle " + intervall);
						player.sendMessage(plugin.getCName() + "Schreibe /rm confirm um es zu mieten.");
					}

					plugin.clicked.put(player.getName(), ms);
					return;
				}
			}
			if (type.equals("sold") && (ms.getOwner().equals(player.getName()) || player.hasPermission("XcraftRegionmarket.Sell.All"))) {
				plugin.clicked.put(player.getName(), ms);
				player.sendMessage(plugin.getCName() + "Möchtest du dein Grundstück wieder verkaufen?");
				player.sendMessage(plugin.getCName() + "Schreibe /rm sell <Preis> um es zum verkauf anzubieten!");
				if (player.hasPermission(plugin.getCName() + ".Dispose")) {
					double price = plugin.marketHandler.getGlobalPrice(ms).getPrice(plugin.regionHandler.getRegion(ms));
					player.sendMessage(plugin.getCName() + "mit /rm dispose kannst du dein Grundstück direkt für " + plugin.getEconomy()
							.format(plugin.configHandler.getDispose(price)) + " verkaufen");
					return;
				}
			}
			if (type.equals("rented") && (ms.getOwner().equals(player.getName()) || player.hasPermission("XcraftRegionmarket.Rent.All"))) {
				plugin.clicked.put(player.getName(), ms);
				player.sendMessage(plugin.getCName() + "Möchtest du dieses Grundstück nicht mehr mieten?");
				player.sendMessage(plugin.getCName() + "Schreibe /rm stop um das Grundstück abzugeben");
				return;
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!(event.getBlock().getState() instanceof Sign)) return;
		MarketSign ms = plugin.marketHandler.getMarketSign(event.getBlock());
		if (ms == null) ms = plugin.rentHandler.getRent(event.getBlock());
		if (ms == null) return;
		if (((ms.getOwner().equals(event.getPlayer().getName()) && event.getPlayer().hasPermission("XcraftRegionMarket.Delete")) || event
				.getPlayer().hasPermission("XcraftRegionMarket.Delete.Other"))) {
			if (event.getPlayer().hasPermission("XcraftRegionMarket.Delete.Other") && !ms.getOwner().equals(event.getPlayer().getName())) plugin
			.getEconomy().depositPlayer(ms.getOwner(), ms.getPrice());
			ProtectedRegion region = plugin.regionHandler.getRegion(ms);
			plugin.regionHandler.removeGroup(region, "xrm");
			plugin.regionHandler.removeAllPlayers(region);
			plugin.regionHandler.saveRegion(event.getBlock().getWorld());
			plugin.marketHandler.remove(ms);
			event.getPlayer().sendMessage(plugin.getCName() + "RegionMarket gelöscht!");
		} else {
			event.getPlayer().sendMessage(plugin.getCName() + ChatColor.RED + "ERROR: Du hast keine Rechte MarketSigns zu löschen!");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		ProtectedRegion region = null;

		if (!event.getLine(0).equals("[sell]") && !event.getLine(0).equals("[rent]")) {
			return;
		}

		// Region check
		Block block = event.getBlock();
		RegionManager mgr = plugin.getWorldguard().getRegionManager(block.getWorld());
		region = mgr.getRegion(event.getLine(1));
		if (region == null) {
			player.sendMessage(plugin.getCName() + ChatColor.RED + "ERROR: Es konnte keine Region unter der ID " + event.getLine(1) + " gefunden werden.");
			event.setCancelled(true);
			return;
		}
		if (!region.getOwners().getPlayers().contains(player.getName()) && !player.hasPermission("XcraftRegionMarket.Sell.Other")) {
			player.sendMessage(plugin.getCName() + ChatColor.RED + "ERROR: Du hast keine Rechte für diese Region");
			return;
		}
		if (!player.hasPermission("XcraftRegionMarket.Create")) {
			player.sendMessage(plugin.getCName() + ChatColor.RED + "ERROR: Du hast keine Rechte MarketSigns zu erstellen!");
			return;
		}
		for (MarketSign sign : plugin.marketHandler.getMarketSigns()) {
			if (sign.getRegion().equals(region.getId())) {
				player.sendMessage(plugin.getCName() + ChatColor.RED + "ERROR: Es gibt bereits ein MarketSign für diese Region");
				event.setCancelled(true);
				return;
			}
		}
		// Player check
		String playername = event.getLine(3);
		if (playername.isEmpty() && player.hasPermission("XcraftRegionMarket.Sell.All")) playername = player.getName();

		// Price check
		int price = 0;
		String id = event.getLine(2).split(":")[0];
		Globalprice gp = null;
		if (!id.matches("\\d*")) {
			gp = plugin.marketHandler.getGlobalPrice(id);
			if (gp == null || !player.hasPermission("XcraftRegionMarket.GP.Use")) {
				event.getPlayer().sendMessage(plugin.getCName() + ChatColor.RED + "ERROR: Unbekannter Preis");
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
			if (day > 1) intervall = day + " Tage & ";
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
		player.sendMessage(plugin.getCName() + "RegionMarket wurde erstellt!");
	}

}
