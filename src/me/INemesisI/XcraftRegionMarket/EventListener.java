package me.INemesisI.XcraftRegionMarket;

import java.util.Date;

import me.INemesisI.XcraftRegionMarket.MarketSign.Type;

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

	// TODO: OnWorldLoad load Signs! Unload aswell!
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && (event.getClickedBlock().getState() instanceof Sign)) {
			MarketSign ms = plugin.marketHandler.getMarketSign(event.getClickedBlock());
			if (ms == null) {
				return;
			}
			Player player = event.getPlayer();
			plugin.clicked.put(player.getName(), ms);
			if (player.hasPermission("XcraftRegionMarket.Mod")) {
				player.sendMessage(plugin.getCName() + "MarketSign Info:");
				player.sendMessage(plugin.getCName() + "Type: " + ChatColor.GOLD + ms.getType());
				player.sendMessage(plugin.getCName() + "Region: " + ChatColor.GOLD + ms.getRegion().getId());
				player.sendMessage(plugin.getCName() + "Price: " + ChatColor.GOLD
						+ plugin.getEconomy().format(ms.getPrice()));
				player.sendMessage((plugin.getCName() + "Globalprice: " + ChatColor.GOLD + ms.getGp()) != null ? ms
						.getGp().getID() : "none");
				player.sendMessage(plugin.getCName() + "Owner: " + ChatColor.GOLD + ms.getOwner());
				if (ms instanceof RentSign) {
					RentSign rent = (RentSign) ms;
					player.sendMessage(plugin.getCName() + "Intervall: " + ChatColor.GOLD + rent.getIntervall());
					player.sendMessage(plugin.getCName() + "Renter: " + ChatColor.GOLD + rent.getRenter());
					player.sendMessage(plugin.getCName() + "Paytime: " + ChatColor.GOLD + rent.getPaytime());
				}
			} else if (ms.getType() == Type.SELLING) {
				if (ms.getOwner().equals(player.getName())) {
					player.sendMessage(plugin.getCName() + "Möchtest du dieses Grundstück nicht mehr verkaufen?");
					player.sendMessage(plugin.getCName() + "Schreibe /rm stop um den verkauf zu stoppen");
					plugin.clicked.put(player.getName(), ms);
				} else if (player.hasPermission("XcraftRegionMarket.Buy")) {
					player.sendMessage(plugin.getCName() + "Möchtest du dieses Grundstück kaufen?");
					player.sendMessage(plugin.getCName() + "Preis: " + ChatColor.GOLD
							+ plugin.getEconomy().format(ms.getPrice()));
					player.sendMessage(plugin.getCName() + "Schreibe /rm confirm um es zu kaufen.");
				}
			} else if ((ms.getType() == Type.RENTING) && player.hasPermission("XcraftRegionMarket.Rent")) {
				String intervall = ((RentSign) ms).getIntervall();
				if (!intervall.isEmpty()) {
					String[] split = intervall.split(" & ");
					if (split[0].substring(0, 1).equals("0")) {
						intervall = split[1];
					}
					if (split[1].substring(0, 1).equals("0")) {
						intervall = split[0];
					}
				}
				player.sendMessage(plugin.getCName() + "Möchtest du dieses Grundstück mieten?");
				player.sendMessage(plugin.getCName() + "Preis: " + ChatColor.GOLD
						+ plugin.getEconomy().format(ms.getPrice()) + " alle " + intervall);
				player.sendMessage(plugin.getCName() + "Schreibe /rm confirm um es zu mieten.");
			} else if ((ms.getType() == Type.SOLD)
					&& (ms.getOwner().equals(player.getName()) || player.hasPermission("XcraftRegionmarket.Sell.All"))) {
				player.sendMessage(plugin.getCName() + "Möchtest du dein Grundstück wieder verkaufen?");
				player.sendMessage(plugin.getCName() + "Schreibe /rm sell <Preis> um es zum verkauf anzubieten!");
				if (player.hasPermission("XcraftRegionmarket.Dispose") && (ms.getGp() != null)) {
					ms.updatePrice();
					double price = plugin.configHandler.getDispose(ms.getPrice());
					player.sendMessage(plugin.getCName() + "mit /rm dispose kannst du dein Grundstück direkt für "
							+ plugin.getEconomy().format(price) + " verkaufen");
				}
			} else if ((ms.getType() == Type.RENTED)
					&& (ms.getOwner().equals(player.getName()) || player.hasPermission("XcraftRegionmarket.Rent.All"))) {
				player.sendMessage(plugin.getCName() + "Möchtest du dieses Grundstück nicht mehr mieten?");
				player.sendMessage(plugin.getCName() + "Schreibe /rm stop um das Grundstück abzugeben");
			}
			return;
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!(event.getBlock().getState() instanceof Sign)) {
			return;
		}
		MarketSign ms = plugin.marketHandler.getMarketSign(event.getBlock());
		if (ms == null) {
			return;
		}

		if (ms.getOwner().equals(event.getPlayer().getName())
				&& event.getPlayer().hasPermission("XcraftRegionMarket.Delete")) {
			ProtectedRegion region = ms.getRegion();
			plugin.regionHandler.removeXRM(region, ms.type);
			plugin.regionHandler.removeAllPlayers(region);
			plugin.regionHandler.saveRegion(event.getBlock().getWorld());
			plugin.marketHandler.remove(ms);
			event.getPlayer().sendMessage(plugin.getCName() + "RegionMarket gelöscht!");
		}
		if (!ms.getOwner().equals(event.getPlayer().getName())
				&& event.getPlayer().hasPermission("XcraftRegionMarket.Delete.All")) {
			plugin.getEconomy().depositPlayer(ms.getOwner(), ms.getPrice());
			event.getPlayer().sendMessage(
					plugin.getCName() + ms.getOwner() + " wurden " + plugin.getEconomy().format(ms.getPrice())
							+ " gutgeschrieben");
		} else {
			event.getPlayer().sendMessage(
					plugin.getCName() + ChatColor.RED + "ERROR: Du hast keine Rechte MarketSigns zu löschen!");
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
			player.sendMessage(plugin.getCName() + ChatColor.RED + "ERROR: Es konnte keine Region unter der ID "
					+ event.getLine(1) + " gefunden werden.");
			event.setCancelled(true);
			return;
		}
		if (!region.getOwners().getPlayers().contains(player.getName())
				&& !player.hasPermission("XcraftRegionMarket.Sell.All")) {
			player.sendMessage(plugin.getCName() + ChatColor.RED + "ERROR: Du hast keine Rechte für diese Region");
			return;
		}
		if (!player.hasPermission("XcraftRegionMarket.Create")) {
			player.sendMessage(plugin.getCName() + ChatColor.RED
					+ "ERROR: Du hast keine Rechte MarketSigns zu erstellen!");
			return;
		}
		for (MarketSign sign : plugin.marketHandler.getAllMarketSigns()) {
			if (sign.getRegion().equals(region.getId())) {
				player.sendMessage(plugin.getCName() + ChatColor.RED
						+ "ERROR: Es gibt bereits ein MarketSign für diese Region");
				event.setCancelled(true);
				return;
			}
		}
		// Player check
		String playername = event.getLine(3);
		if (!playername.isEmpty() && !playername.equals(player.getName())
				&& !player.hasPermission("XcraftRegionMarket.Sell.All")) {
			player.sendMessage(plugin.getCName() + ChatColor.RED
					+ "ERROR: Du kannst keine Region für andere Spieler erstellen!");
			event.setCancelled(true);
			return;
		}

		// Price check
		int price = 0;
		String id = event.getLine(2).split(":")[0];
		Globalprice gp = null;
		if (!id.matches("\\d*")) {
			gp = plugin.marketHandler.getGlobalPrice(id);
			if ((gp != null) || player.hasPermission("XcraftRegionMarket.GP.Use")) {
				price = gp.getPrice();
			} else {
				event.getPlayer().sendMessage(plugin.getCName() + ChatColor.RED + "ERROR: Unbekannter Preis");
				event.setCancelled(true);
				return;
			}
		} else {
			price = Integer.parseInt(id);
		}

		// Create the MarketSign
		if (event.getLine(0).equals("[sell]")) {
			SellSign sign = new SellSign(block, region, Type.SELLING, playername, price, gp);
			plugin.marketHandler.add(sign);
			plugin.marketHandler.update(sign);
			plugin.regionHandler.addXRM(region, sign.type);
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
			if (day > 1) {
				intervall = day + " Tage & ";
			} else {
				intervall = day + " Tag & ";
			}
			intervall = intervall + hour + " Std.";
			RentSign sign = new RentSign(block, region, Type.RENTING, price, gp, playername, intervall, new Date());
			plugin.marketHandler.add(sign);
			plugin.marketHandler.update(sign);
			plugin.regionHandler.addXRM(region, sign.type);
		}
		player.sendMessage(plugin.getCName() + "RegionMarket wurde erstellt!");
	}
}
