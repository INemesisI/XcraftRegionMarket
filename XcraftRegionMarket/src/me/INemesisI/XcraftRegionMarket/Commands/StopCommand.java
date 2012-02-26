package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.Commands.CommandHelper;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.Rent;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopCommand extends CommandHelper {

	protected StopCommand(XcraftRegionMarket instance) {
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
		if (!ms.getType().equals("sell") && !ms.getType().equals("rented")) {
			reply("Dieses Schild verkauft keine Region");
			return;
		}
		if (ms.getType().equals("sell")) {
			ms.setType("sold");
			plugin.marketHandler.update(ms);
			reply("Deine Region wird ab jetzt nicht mehr zum Verkauf angeboten");
		}
		
		if (ms.getType().equals("rented")) {
			ms.setType("rent");
			plugin.regionHandler.removeOwner(plugin.regionHandler.getRegion(ms), ms.getOwner());
			plugin.regionHandler.saveRegion(player.getWorld());
			Rent rent = plugin.rentHandler.getRent(ms.getBlock());
			ms.setOwner(rent.getRenter());
			plugin.rentHandler.remove(rent);
			plugin.marketHandler.add(ms);
			plugin.marketHandler.update(ms);
			reply("Deine Region wurde abgegeben, du musst keine Miete mehr zahlen");
			// Remove the given group
			for (String group : plugin.groupHandler.getPlayerGroups(sender.getName())) {
				if (plugin.configHandler.getRentgroups().containsKey(group) && plugin.configHandler.getRentgroups().containsValue(
						plugin.regionHandler.getRegion(ms).getParent().getId())) {
					plugin.groupHandler.removePlayerFromGroup(player.getName(), group);
				}
			}
		}
	}
}
