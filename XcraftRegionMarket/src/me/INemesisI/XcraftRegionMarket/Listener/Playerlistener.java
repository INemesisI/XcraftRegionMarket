package me.INemesisI.XcraftRegionMarket.Listener;

import java.text.SimpleDateFormat;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.Rent;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class Playerlistener extends PlayerListener {
	private XcraftRegionMarket plugin;

	public Playerlistener(XcraftRegionMarket instance) {
		plugin = instance;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				&& event.getClickedBlock().getState() instanceof Sign) {
			MarketSign ms = plugin.marketHandler.getMarketSign(event
					.getClickedBlock());
			if (ms == null)
				ms = plugin.rentHandler.getRent(event.getClickedBlock());
			if (ms == null)
				return;

			String type = ms.getType();
			Player player = event.getPlayer();
			if (player.hasPermission("XcraftRegionMarket.Mod")) {
				player.sendMessage(plugin.getName() + "MarketSign Info:");
				player.sendMessage(plugin.getName() + "Type: " + ChatColor.GOLD+ ms.getType());
				player.sendMessage(plugin.getName() + "Region: "+ ChatColor.GOLD + ms.getRegion());
				String price = plugin.getEconomy().format(ms.getPrice());
				for (Globalprice gp : plugin.marketHandler.getGlobalPrices()) {
					if(!ms.getType().equals("rented")) {
						for (MarketSign gpms : gp.getMarketSigns()) {
							if (gpms.equals(ms))
								price += " (GP: "+gp.toString()+")";
						}
					}
				}
				player.sendMessage(plugin.getName() + "Price: "+ ChatColor.GOLD + price);
				player.sendMessage(plugin.getName() + "Owner: "+ ChatColor.GOLD + ms.getOwner());
				if (ms.getType().equals("rented")|| ms.getType().equals("rent"))
					player.sendMessage(plugin.getName() + "Intervall: "+ ChatColor.GOLD + ms.getIntervall());
				Rent rent = plugin.rentHandler.getRent(ms.getBlock());
				if (rent != null) {
					player.sendMessage(plugin.getName() + "Renter: "+ ChatColor.GOLD + rent.getRenter());
					SimpleDateFormat date = new SimpleDateFormat();
					date.applyPattern("yyyy.MM.dd HH:mm");
					player.sendMessage(plugin.getName() + "Paytime: "+ ChatColor.GOLD + date.format(rent.getPaytime()));
				}
				plugin.clicked.put(player.getName(), ms);
				return;
			}
			if (type.equals("sell")
					&& (player.hasPermission("XcraftRegionMarket.Buy"))|| (type.equals("rent") && player.hasPermission("XcraftRegionMarket.Rent"))) {
				if (type.equals("sell") && ms.getOwner().equals(player.getName())) {
					player.sendMessage(plugin.getName()+ "Möchtest du dieses Grundstück nicht mehr verkaufen?");
					player.sendMessage(plugin.getName()+ "Schreibe /rm stop um den verkauf zu stoppen");
					plugin.clicked.put(player.getName(), ms);
					return;
				}
				if (ms.getType().equals("sell") || ms.getType().equals("rent")) {
					if (ms.getType().equals("sell")) {
						player.sendMessage(plugin.getName() + "Möchtest du dieses Grundstück kaufen?");
						player.sendMessage(plugin.getName() + "Preis: "+ ChatColor.GOLD + plugin.getEconomy().format(ms.getPrice()));
						player.sendMessage(plugin.getName() + "Schreibe /rm confirm um es zu kaufen.");
					}
					if (ms.getType().equals("rent")) {
						String intervall = ms.getIntervall();
						if (!intervall.isEmpty()) {
							String[] split = intervall.split(" & ");
							if (split[0].substring(0, 1).equals("0"))
								intervall = split[1];
							if (split[1].substring(0, 1).equals("0"))
								intervall = split[0];
						}
						player.sendMessage(plugin.getName() + "Möchtest du dieses Grundstück mieten?");
						player.sendMessage(plugin.getName() + "Preis: " + ChatColor.GOLD + plugin.getEconomy().format(ms.getPrice()) + " alle " + intervall);
						player.sendMessage(plugin.getName() + "Schreibe /rm confirm um es zu mieten.");
					}

					plugin.clicked.put(player.getName(), ms);
					return;
				}
			}
			if (type.equals("sold")
					&& (ms.getOwner().equals(player.getName()) || player.hasPermission("XcraftRegionmarket.Sell.All"))) {
				plugin.clicked.put(player.getName(), ms);
				player.sendMessage(plugin.getName() + "Möchtest du dein Grundstück wieder verkaufen?");
				player.sendMessage(plugin.getName() + "Schreibe /rm sell <Preis> um es zu verkaufen!");
				return;
			}
			if (type.equals("rented")&& (ms.getOwner().equals(player.getName()) || player.hasPermission("XcraftRegionmarket.Rent.All"))) {
				plugin.clicked.put(player.getName(), ms);
				player.sendMessage(plugin.getName() + "Möchtest du dieses Grundstück nicht mehr mieten?");
				player.sendMessage(plugin.getName() + "Schreibe /rm stop um das Grundstück abzugeben");
				return;
			}
		}
	}

}
