package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.Commands.CommandHelper;
import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.Rent;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class EditCommands extends CommandHelper {

	public EditCommands(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	public void execute(CommandSender sender, String Command, List<String> list) {
		this.sender = sender;
		this.player = (Player) sender;
		this.worldguard = plugin.getWorldguard();

		MarketSign ms = getClicked().get(sender.getName());
		if (ms == null) {
			error("Du musst erst auf ein Schild klicken!");
			return;
		}

		if (Command.equals("settype")) {
			String type = list.get(0);
			if (!type.equals("sell") && !type.equals("rent") && !type.equals("sold") && !type.equals("rented")) {
				error("Unbekannter Typ: " + list.get(0));
				return;
			}
			ms.setType(type);
			plugin.marketHandler.update(ms);
			if (type.equals("rented")) {
				Rent rent = new Rent(ms.getBlock(), ms.getRegion(), ms.getOwner(), ms.getPrice(), ms.getIntervall());
				rent.setRenter(ms.getOwner());
				plugin.rentHandler.add(rent);
				plugin.marketHandler.remove(ms);
			}
			reply("Der Typ wurde erfolgreich ge�ndert!");
		}

		if (Command.equals("setregion")) {
			ProtectedRegion region = plugin.regionHandler.getRegion(ms);
			if (region == null)
				error("Es konnte keine Region unter der ID " + list.get(0) + " gefunden werden.");
			else {
				ms.setRegion(list.get(0));
				plugin.marketHandler.update(ms);
				reply("Die Region wurde erfolgreich ge�ndert!");
			}
			return;
		}

		if (Command.equals("setprice")) {
			double price = 0;
			if (!list.get(0).matches("\\d*")) {
				Globalprice gp = plugin.marketHandler.getGlobalPrice(list.get(0));
				for (Globalprice gpr : plugin.marketHandler.getGlobalPrices()) {
					gpr.removeSign(ms);
				}
				if (gp != null && player.hasPermission("XcraftRegionMarket.GP.Use")) {
					gp.addSign(ms);
					ms.setPrice(gp.getPrice(plugin.regionHandler.getRegion(ms)));
				} else {
					reply("Der Preis (" + list.get(0) + ") darf nur Zahlen enthalten.");
					return;
				}
			} else {
				price = Double.parseDouble(list.get(0));
				ms.setPrice(price);
			}
			plugin.marketHandler.update(ms);
			reply("Der Preis wurde erfolgreich ge�ndert!");
			return;
		}
		if (Command.equals("setowner")) {
			ProtectedRegion region = plugin.regionHandler.getRegion(ms);
			plugin.regionHandler.setPlayer(region, list.get(0));
			ms.setOwner(list.get(0));
			plugin.marketHandler.update(ms);
			reply("Der Besitzer wurde erfolgreich ge�ndert!");
			return;
		}

		if (Command.equals("setintervall")) {
			if (ms.getType().equals("rented") || ms.getType().equals("rent")) {
				error("Dieses Schild muss vom Typ \"rent\" oder \"rented\" sein!");
				return;
			}
			String intervall = list.get(0);
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
			ms.setIntervall(intervall);
			plugin.marketHandler.update(ms);
			reply("Das Intervall wurde erfolgreich ge�ndert!");
			return;
		}

		if (Command.equals("setRenter")) {
			Rent rent = plugin.rentHandler.getRent(ms.getBlock());
			if (rent == null) {
				error("Dieses Schild muss gemietet worden sein!");
				return;
			}
			rent.setRenter(list.get(0));
			reply("Der Vermieter wurde erfolgreich ge�ndert!");
			return;
		}
	}
}
