package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.MarketSign.Type;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;

public class SellCommand extends CommandHelper {

	protected SellCommand(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	protected void execute(CommandSender sender, String Command, List<String> list) {
		this.init(sender);

		MarketSign ms = this.getClicked().get(sender.getName());
		if (ms == null) {
			this.error("Du musst erst ein MarketSign auswählen!");
			return;
		}
		if (!ms.getOwner().equals(player.getName()) && !player.hasPermission("XcraftRegionMarket.Sell.All")) {
			this.reply("Das ist nicht deine Region!");
			return;
		}
		double price = 0;
		if (!list.get(0).matches("\\d*")) {
			Globalprice gp = plugin.marketHandler.getGlobalPrice(list.get(0));
			if ((gp != null) && player.hasPermission("XcraftRegionMarket.GP.Use")) {
				ms.setGp(gp);
				ms.updatePrice();
			} else {
				this.reply("Unbekannter Preis (" + list.get(0) + ")");
				return;
			}
		} else {
			price = Double.parseDouble(list.get(0));
			ms.setPrice(price);
		}
		ms.setType(Type.SELLING);
		plugin.marketHandler.update(ms);
		this.reply("Deine Region wird ab jetzt für " + economy.format(price) + " angeboten");
	}
}
