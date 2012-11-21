package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class PlayerCommands extends CommandHelper {

	public PlayerCommands(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	public void execute(CommandSender sender, String Command, List<String> list) {
		this.sender = sender;
		this.player = (Player) sender;
		this.worldguard = plugin.getWorldguard();

		MarketSign ms = getClicked().get(sender.getName());
		if (ms == null) {
			error("Du musst erst auf ein Schild klicken!");
			return;
		}
		if (!ms.getOwner().equals(player.getName()) && !player.hasPermission("XcraftRegionMarket.AddPlayer.Other")) {
			reply("Das ist nicht dein Schild!");
			return;
		}
		Player member = plugin.getServer().getPlayer(list.get(0));
		if (member == null) {
			error("Der Spieler muss dafür online sein!");
			return;
		}
		ProtectedRegion region = plugin.regionHandler.getRegion(ms);

		if (Command.equals("addplayer")) {
			plugin.regionHandler.addMember(region, member.getName());
			plugin.regionHandler.saveRegion(player.getWorld());
			reply(member.getName() + " wurde zu deiner Region hinzugefügt");
		}

		if (Command.equals("removeplayer")) {
			plugin.regionHandler.removeMember(region, member.getName());
			plugin.regionHandler.saveRegion(player.getWorld());
			reply(member.getName() + " wurde von deiner Region gel�scht");
		}

		// TODO: /giveregion
	}
}
