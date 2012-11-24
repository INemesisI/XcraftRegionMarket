package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.MarketSign.Type;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class DisposeCommand extends CommandHelper {

	public DisposeCommand(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	public void execute(CommandSender sender, String Command, List<String> args) {
		init(sender);

		MarketSign ms = getClicked().get(sender.getName());
		if (ms == null) {
			error("Du musst erst ein MarketSign auswählen!");
			return;
		}
		if (!ms.getOwner().equals(player.getName()) && !player.hasPermission("XcraftRegionMarket.Sell.All")) {
			error("Dies ist nicht deine Region!");
			return;
		}
		if (!ms.getType().equals("sold")) {
			error("Die Region muss gekauft worden sein.");
			return;
		}

		ProtectedRegion region = ms.getRegion();
		double dispose = getConfigHandler().getDispose(ms.getPrice());
		// set money;
		economy.depositPlayer(ms.getOwner(), dispose);
		// set regionowner
		plugin.regionHandler.removeAllPlayers(region);
		// set sign
		ms.setOwner(plugin.configHandler.getServerAccount());
		ms.setType(Type.SELLING);
		getMarketHandler().update(ms);

		reply("Deine Region wurde für " + economy.format(dispose) + " an den Server verkauft!");
	}
}
