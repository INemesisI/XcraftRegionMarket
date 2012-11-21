package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
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
			getConfigHandler().save();
			reply("Saved the data to config file");
		}
		if (Command.equals("reload")) {
			getConfigHandler().load();
			reply("Files reloaded");
		}
		if (Command.equals("update")) {
			for (Globalprice gpr : getMarketHandler().getGlobalPrices()) {
				for (MarketSign ms : gpr.getMarketSigns()) {
					ms.setPrice(gpr.getPrice(plugin.regionHandler.getRegion(ms)));
				}
			}
			for (MarketSign ms : getMarketHandler().getMarketSigns()) {
				getMarketHandler().update(ms);
			}
		}
	}
}
