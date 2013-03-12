package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;

public class GPCommands extends CommandHelper {

	public GPCommands(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	protected void execute(CommandSender sender, String Command, List<String> list) {
		this.init(sender);

		if (Command.equals("creategp")) {
			if (this.getGP(list.get(0)) != null) {
				this.reply("Es gibt bereits einen GlobalPrice mit der ID");
				return;
			}
			if (list.get(1).matches("\\d*")) {
				this.addGP(list.get(0), Integer.parseInt(list.get(1)));
				this.reply("GlobalPrice " + list.get(0) + " wurde erstellt!");
			} else {
				this.reply("Der Preis (" + list.get(1) + ") darf nur Zahlen enthalten...");
			}
		}
		if (Command.equals("setgp")) {
			Globalprice gp = this.getGP(list.get(0));
			if (gp == null) {
				this.reply("Ein GlobalPrice mit der ID " + list.get(0) + " konnte nicht gefunden werden.");
				return;
			}
			if (!list.get(1).matches("\\d*")) {
				this.reply("Der Preis (" + list.get(1) + ") darf nur Zahlen enthalten...");
				return;
			}
			gp.setPrice(Integer.parseInt(list.get(1)));
			plugin.marketHandler.updateAll(gp);
			this.reply("Der Preis wurd ge�ndert");
		}
		if (Command.equals("listgp") && player.hasPermission("XcraftRegionMarket.GP.Use")) {
			this.reply("Globalprices: " + this.getGP().toString());
		}

	}

}
