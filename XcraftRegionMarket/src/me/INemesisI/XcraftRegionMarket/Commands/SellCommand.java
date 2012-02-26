package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.Commands.CommandHelper;
import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SellCommand extends CommandHelper {

	protected SellCommand(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	protected void execute(CommandSender sender, String Command, List<String> list) {
		this.sender = sender;
		this.player = (Player) sender;
		this.economy = plugin.getEconomy();
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
}
