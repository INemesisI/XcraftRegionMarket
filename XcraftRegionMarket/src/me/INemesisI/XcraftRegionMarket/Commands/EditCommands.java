package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.MarketSign.Type;
import me.INemesisI.XcraftRegionMarket.RentSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class EditCommands extends CommandHelper {

	public EditCommands(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	public void execute(CommandSender sender, String Command, List<String> list) {
		init(sender);

		MarketSign ms = getClicked().get(sender.getName());
		if (ms == null) {
			error("Du musst erst ein MarketSign auswählen!");
			return;
		}

		if (Command.equals("settype")) {
			Type type = Type.valueOf(list.get(0).toUpperCase());
			if (type == null) {
				error("Unbekannter Typ: " + list.get(0));
				return;
			}
			ms.setType(type);
			plugin.marketHandler.update(ms); // TODO: TEST FOR RENTED / RENT
												// (Owner problems?)
			reply("Der Typ wurde erfolgreich ge�ndert!");
		}

		if (Command.equals("setregion")) {
			ProtectedRegion region = plugin.regionHandler.getRegion(player.getWorld(), list.get(0));
			if (region == null) error("Es konnte keine Region unter der ID " + list.get(0) + " gefunden werden.");
			else {
				ms.setRegion(region);
				plugin.marketHandler.update(ms);
				reply("Die Region wurde erfolgreich geändert!");
			}
			return;
		}

		if (Command.equals("setprice")) {
			double price = 0;
			if (!list.get(0).matches("\\d*")) {
				Globalprice gp = plugin.marketHandler.getGlobalPrice(list.get(0));
				if (gp != null && player.hasPermission("XcraftRegionMarket.GP.Use")) ms.setGp(gp);
				else {
					error("Der Preis (" + list.get(0) + ") darf nur Zahlen enthalten.");
					return;
				}
			} else ms.setPrice(Double.parseDouble(list.get(0)));
			plugin.marketHandler.update(ms);
			reply("Der Preis wurde erfolgreich ge�ndert!");
			return;
		}
		if (Command.equals("setowner")) {
			ProtectedRegion region = ms.getRegion();
			plugin.regionHandler.setPlayer(region, list.get(0));
			ms.setOwner(list.get(0));
			plugin.marketHandler.update(ms);
			reply("Der Besitzer wurde erfolgreich ge�ndert!");
			return;
		}

		if (Command.equals("setintervall")) {
			if (ms.getType() == Type.SELLING || ms.getType() != Type.SOLD) {
				error("Dieses Schild muss vom Typ \"rent\" oder \"rented\" sein!");
				return;
			}
			String intervall = list.get(0);
			int day = 0;
			int hour = 0;
			for (String s : list)
				if (s.contains("w") || s.contains("week")) {
					int i = s.indexOf("w");
					day += Integer.parseInt(s.substring(0, i).trim()) * 7;
				} else if (s.contains("d") || s.contains("day")) {
					int i = s.indexOf("d");
					day += Integer.parseInt(s.substring(0, i).trim());
				} else if (s.contains("h") || s.contains("hour")) {
					int i = s.indexOf("h");
					hour += Integer.parseInt(s.substring(0, i).trim());
				}
			if (day > 1) intervall = day + " Tage & ";
			else intervall = day + " Tag & ";
			intervall = intervall + hour + " Std.";
			((RentSign) ms).setIntervall(intervall);
			plugin.marketHandler.update(ms);
			reply("Das Intervall wurde erfolgreich ge�ndert!");
			return;
		}

		if (Command.equals("setRenter")) {
			if (ms.getType() == Type.SELLING || ms.getType() != Type.SOLD) {
				error("Dieses Schild muss vom Typ \"rent\" oder \"rented\" sein!");
				return;
			}
			((RentSign) ms).setRenter(list.get(0));
			reply("Der Vermieter wurde erfolgreich ge�ndert!");
			return;
		}
	}
}
