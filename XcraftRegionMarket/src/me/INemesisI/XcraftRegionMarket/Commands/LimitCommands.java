package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;
import java.util.Map;

import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LimitCommands extends CommandHelper {

	public LimitCommands(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	protected void execute(CommandSender sender, String Command, List<String> list) {
		this.sender = sender;
		this.player = (Player) sender;

		// /rm limit list <sell/rent>
		// /rm limit <region> <sell/rent> <int>
		// /rm limit <region> <sell/rent>

		if (list.get(0).equals("list")) {
			Map<String, Integer> limit = null;
			if (list.get(1).equals("sell")) {
				limit = getConfigHandler().getSelllimit();
				reply("Sell-Limit list:");
			} else if (list.get(1).equals("rent")) {
				limit = getConfigHandler().getRentlimit();
				reply("Rent-Limit list:");
			} else {
				error("/rm limit list <sell/rent>");
			}
			String output = "[";
			for (String parent : limit.keySet()) {
				output += parent + ":" + limit.get(parent) + "; ";
			}
			reply(output + "]");
		}

		if (list.get(0).equals("set")) {

		}
	}
}
