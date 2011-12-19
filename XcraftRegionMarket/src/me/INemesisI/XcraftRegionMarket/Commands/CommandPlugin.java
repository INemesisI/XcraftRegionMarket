package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.Rent;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;
import me.INemesisI.XcraftRegionMarket.Handler.ConfigHandler;

import org.bukkit.command.CommandSender;

public class CommandPlugin extends CommandHelper {

	public CommandPlugin(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	public void execute(CommandSender sender, String Command, List<String> list) {
		this.sender = sender;
		if (Command.equals("save")) {
			plugin.configHandler.save();
			reply("Saved the data to config file");
		}
		if (Command.equals("reload")) {
			plugin.configHandler = new ConfigHandler(plugin);
			plugin.configHandler.load();
			reply("Files reloaded");
		}
		if (Command.equals("update")) {
			for (Globalprice gpr : plugin.marketHandler.getGlobalPrices()) {
				for (MarketSign ms : gpr.getMarketSigns()) {
					ms.setPrice(gpr.getPrice(plugin.regionHandler.getRegion(ms)));
				}
				for (Rent rent : gpr.getRents()) {
					rent.setPrice(gpr.getPrice(plugin.regionHandler.getRegion(rent)));
				}
			}
			for (MarketSign ms : plugin.marketHandler.getMarketSigns()) {
				plugin.marketHandler.update(ms);
			}
		}
	}
}
