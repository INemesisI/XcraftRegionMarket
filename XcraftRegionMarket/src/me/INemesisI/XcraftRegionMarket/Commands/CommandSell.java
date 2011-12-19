package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.Rent;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSell extends CommandHelper {

	protected CommandSell(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	protected void execute(CommandSender sender, String Command, List<String> list) {
		this.sender = sender;
		this.player = (Player) sender;
		this.economy = plugin.getEconomy();
		this.permission = plugin.getPermission();
		this.worldguard = plugin.getWorldguard();
		MarketSign ms = getClicked().get(sender.getName());
		if (ms == null) {
			error("Du musst erst auf ein Schild klicken!");
			return;
		}
		if (!ms.getOwner().equals(player.getName()) && !player.hasPermission("XcraftRegionMarket.Sell.All")) {
			reply("Das ist nicht deine Region!");
			return;
		}
		if (Command.contains("sell")) {
			if (!ms.getType().equals("sold")) {
				reply("Die Region muss gekauft worden sein.");
				return;
			}
			double price = 0;
			if (!list.get(0).matches("\\d*")) {
				Globalprice gp = plugin.marketHandler.getGlobalPrice(list.get(0));
				if (gp != null && player.hasPermission("XcraftRegionMarket.GP.Use")) {
					gp.addSign(ms);
					ms.setPrice(gp.getPrice(plugin.regionHandler.getRegion(ms)));
				} else {
					reply("Unbekannter Preis (" + list.get(0) + ")");
					return;
				}
			} else {
				price = Double.parseDouble(list.get(0));
				ms.setPrice(price);
			}
			ms.setType("sell");
			plugin.marketHandler.update(ms);
			reply("Deine Region wird ab jetzt für " + economy.format(price) + " angeboten");
		}
		if (Command.contains("stop")) {
			if (!ms.getType().equals("sell") && !ms.getType().equals("rented")) {
				reply("Dieses Schild verkauft keine Region");
				return;
			}
			if (ms.getType().equals("sell")) {
				ms.setType("sold");
				plugin.marketHandler.update(ms);
				reply("Deine Region wird ab jetzt nicht mehr zum Verkauf angeboten");
			}
			if (ms.getType().equals("rented")) {
				ms.setType("rent");
				plugin.regionHandler.removeOwner(plugin.regionHandler.getRegion(ms), ms.getOwner());
				plugin.regionHandler.saveRegion(player.getWorld());
				Rent rent = plugin.rentHandler.getRent(ms.getBlock());
				ms.setOwner(rent.getRenter());
				plugin.rentHandler.remove(rent);
				for (Globalprice gp : plugin.marketHandler.getGlobalPrices()) {
					if (gp.removeRent(rent) == true)
						gp.addSign(ms);
				}
				plugin.marketHandler.add(ms);
				plugin.marketHandler.update(ms);
				reply("Deine Region wurde abgegeben, du musst keine Miete mehr zahlen");
				// Remove the last given group
				String group = plugin.configHandler.getRentgroups().get(plugin.regionHandler.getRegionCount(player, player.getWorld(), "rented").get("global")+1);
				if (group != null) {
					permission.playerRemoveGroup((String) null, group, player.getName());
				}
			}
		}

	}

}
