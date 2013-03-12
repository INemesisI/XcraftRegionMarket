package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.List;
import java.util.Map;

import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;

public class LimitCommands extends CommandHelper {

	public LimitCommands(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	protected void execute(CommandSender sender, String Command, List<String> list) {
		this.init(sender);

		// /rm limit list <sell/rent>
		// /rm limit <region> <sell/rent> <int>
		// /rm limit <region> <sell/rent>

		if (list.get(0).equals("list")) {
			Map<String, Integer> limit = null;
			if (list.get(1).equals("sell")) {
				limit = this.getConfigHandler().getSelllimit();
				this.reply("Sell-Limit list:");
			} else if (list.get(1).equals("rent")) {
				limit = this.getConfigHandler().getRentlimit();
				this.reply("Rent-Limit list:");
			} else {
				this.error("/rm limit list <sell/rent>");
			}
			String output = "[";
			for (String parent : limit.keySet()) {
				output += parent + ": " + limit.get(parent) + "; ";
			}
			this.reply(output + "]");
		}

		if (list.get(0).equals("set")) {

		}
	}
}
