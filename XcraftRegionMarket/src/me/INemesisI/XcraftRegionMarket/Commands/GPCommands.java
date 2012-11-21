package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GPCommands extends CommandHelper {

	public GPCommands(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	protected void execute(CommandSender sender, String Command, List<String> list) {
		this.sender = sender;
		this.player = (Player) sender;
		if (Command.equals("creategp") && player.hasPermission("XcraftRegionMarket.GP.Create")) {
			if (list.get(1).matches("\\d*")) {
				if (getGP(list.get(0)) != null) {
					reply("Es gibt bereits einen GlobalPrice mit der ID");
					return;
				}
				addGP(list.get(0), Integer.parseInt(list.get(1)));
				reply("GlobalPrice " + list.get(0) + " wurde erstellt!");
			} else
				reply("Der Preis (" + list.get(1) + ") darf nur Zahlen enthalten...");
		}
		if (Command.equals("setgp") && player.hasPermission("XcraftRegionMarket.GP.Create")) {
			Globalprice gp = getGP(list.get(0));
			if (gp == null) {
				reply("Ein GlobalPrice mit der ID " + list.get(0) + " konnte nicht gefunden werden.");
				return;
			}
			if (!list.get(1).matches("\\d*")) {
				reply("Der Preis (" + list.get(1) + ") darf nur Zahlen enthalten...");
				return;
			}
			gp.setPrice(Integer.parseInt(list.get(1)));
			for (MarketSign ms : gp.getMarketSigns()) {
				ms.setPrice(gp.getPrice(plugin.regionHandler.getRegion(ms)));
				plugin.marketHandler.update(ms);
			}
			reply("Der Preis wurd ge�ndert");
		}
		if (Command.equals("listgp") && player.hasPermission("XcraftRegionMarket.GP.Use")) {
			reply("Globalprices: " + getGP().toString());
		}

	}

}
