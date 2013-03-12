package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;

import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands extends CommandHelper {

	public PlayerCommands(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	public void execute(CommandSender sender, String Command, List<String> list) {
		this.init(sender);

		MarketSign ms = this.getClicked().get(sender.getName());
		if (ms == null) {
			this.error("Du musst erst ein MarketSign auswählen!");
			return;
		}
		if (!ms.getOwner().equals(player.getName()) && !player.hasPermission("XcraftRegionMarket.AddPlayer.Other")) {
			this.reply("Das ist nicht dein Schild!");
			return;
		}
		Player member = plugin.getServer().getPlayer(list.get(0));
		if (member == null) {
			this.error("Der Spieler muss dafür online sein!");
			return;
		}

		if (Command.equals("addplayer")) {
			plugin.regionHandler.addMember(ms.getRegion(), member.getName());
			plugin.regionHandler.saveRegion(player.getWorld());
			this.reply(member.getName() + " wurde zu deiner Region hinzugefügt");
		}

		if (Command.equals("removeplayer")) {
			plugin.regionHandler.removeMember(ms.getRegion(), member.getName());
			plugin.regionHandler.saveRegion(player.getWorld());
			this.reply(member.getName() + " wurde von deiner Region gel�scht");
		}

		// TODO: /giveregion
	}
}
