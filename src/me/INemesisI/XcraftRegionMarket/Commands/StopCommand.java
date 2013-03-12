package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.MarketSign.Type;
import me.INemesisI.XcraftRegionMarket.RentSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;

public class StopCommand extends CommandHelper {

	protected StopCommand(XcraftRegionMarket instance) {
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
		if ((ms.getType() != Type.SELLING) && (ms.getType() != Type.RENTED)) {
			this.reply("Dieses Schild verkauft keine Region");
			return;
		}
		if (ms.getType().equals("sell") && !ms.getOwner().equals(player.getName())
				&& !player.hasPermission("XcraftRegionMarket.Sell.All")) {
			ms.setType(Type.SOLD);
			plugin.marketHandler.update(ms);
			this.reply("Deine Region wird ab jetzt nicht mehr zum Verkauf angeboten");
		}

		if (ms.getType().equals("rented")) {
			RentSign rs = (RentSign) ms;
			if (!rs.getRenter().equals(player.getName()) && !player.hasPermission("XcraftRegionMarket.Sell.All")) {
				ms.setType(Type.RENTING);
				plugin.regionHandler.setPlayer(ms.getRegion(), rs.getRenter());
				plugin.regionHandler.saveRegion(player.getWorld());
				rs.setOwner(rs.getRenter());
				plugin.marketHandler.update(ms);
				this.reply("Deine Region wurde abgegeben, du musst keine Miete mehr zahlen");
				// Remove the given group
				for (String group : plugin.getPermission().getPlayerGroups(player)) {
					if (plugin.configHandler.getRentgroups().containsKey(group)
							&& plugin.configHandler.getRentgroups().get(group)
									.contains(ms.getRegion().getParent().getId())) {
						plugin.getPermission().playerRemoveGroup(player, group);
					}
				}
			}
		}
	}
}
