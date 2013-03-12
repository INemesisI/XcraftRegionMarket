package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;

public class PluginCommands extends CommandHelper {

	public PluginCommands(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	public void execute(CommandSender sender, String Command, List<String> list) {
		this.sender = sender;
		if (Command.equals("save")) {
			this.getConfigHandler().save();
			this.reply("Saved the data to config file");
		}
		if (Command.equals("reload")) {
			this.getConfigHandler().load();
			this.reply("Files reloaded");
		}
		if (Command.equals("update")) {
			for (Globalprice gpr : this.getMarketHandler().getGlobalPrices()) {
				plugin.marketHandler.updateAll(gpr);
			}
		}
	}
}
