package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import me.INemesisI.XcraftRegionMarket.Commands.CommandHelper;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

public class DisposeCommand extends CommandHelper {

	public DisposeCommand(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	public void execute(CommandSender sender, String Command, List<String> args) {
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
			error("Das ist nicht deine Region!");
			return;
		}
		if (!ms.getType().equals("sold")) {
			error("Die Region muss gekauft worden sein.");
			return;
		}

		ms.setType("sell");
		// set money
		economy.getAccount(ms.getOwner()).add(plugin.configHandler.getDispose(ms.getPrice()));
		// set regionowner
		ProtectedRegion region = plugin.regionHandler.getRegion(ms);
		plugin.regionHandler.setPlayer(region, plugin.configHandler.getServerAccount());
		
		plugin.marketHandler.update(ms);
		reply("Deine Region wurde für " + economy.format(plugin.configHandler.getDispose(ms.getPrice())) + " an den Server verkauft!");
	}
}
