package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler extends CommandHelper implements CommandExecutor {
	private static Map<String, CommandHelper> subcommands = new HashMap<String, CommandHelper>();
	private static Map<String, String> permNodes = new HashMap<String, String>();

	public CommandHandler(XcraftRegionMarket instance) {
		super(instance);

		permNodes.put("save", "Save");
		permNodes.put("reload", "Reload");
		permNodes.put("creategp", "GP.Create");
		permNodes.put("setgp", "GP.Edit");
		permNodes.put("listgp", "GP.List");
		permNodes.put("confirm", "Buy");
		permNodes.put("abort", "Buy");
		permNodes.put("sell", "Sell");
		permNodes.put("rent", "Rent");
		permNodes.put("stop", "Sell");
		permNodes.put("addplayer", "AddPlayer");
		permNodes.put("removeplayer", "RemovePlayer");
		permNodes.put("settype", "Edit.Type");
		permNodes.put("setregion", "Edit.Region");
		permNodes.put("setprice", "Edit.Price");
		permNodes.put("setowner", "Edit.Account");
		permNodes.put("setintervall", "Edit.Intervall");
		permNodes.put("setrenter", "Edit.Renter");

		subcommands.put("save", new CommandPlugin(plugin));
		subcommands.put("reload", new CommandPlugin(plugin));
		subcommands.put("creategp", new CommandGP(plugin));
		subcommands.put("setgp", new CommandGP(plugin));
		subcommands.put("listgp", new CommandGP(plugin));
		subcommands.put("confirm", new CommandConfirm(plugin));
		subcommands.put("abort", new CommandConfirm(plugin));
		subcommands.put("sell", new CommandSell(plugin));
		subcommands.put("stop", new CommandSell(plugin));
		subcommands.put("addplayer", new CommandPlayer(plugin));
		subcommands.put("removeplayer", new CommandPlayer(plugin));
		subcommands.put("settype", new CommandEdit(plugin));
		subcommands.put("setregion", new CommandEdit(plugin));
		subcommands.put("setprice", new CommandEdit(plugin));
		subcommands.put("setowner", new CommandEdit(plugin));
		subcommands.put("setintervall", new CommandEdit(plugin));
		subcommands.put("setrenter", new CommandEdit(plugin));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		this.sender = sender;
		player = (sender instanceof Player) ? (Player) sender : null;

		if (player == null) {
			// TODO: Console Stuff
		} else if (args.length == 0 || args[0].equals("help")) {
			PrintHelp();
			return true;
		} else if (subcommands.get(args[0].toLowerCase()) == null) {
			error("Unkown command: " + args[0].toLowerCase());
		} else if (!(permNodes.get(args[0]).isEmpty() || player.hasPermission(plugin.getDescription().getName() + "." + permNodes.get(args[0])) || player.isOp())) {
			error("You do not have access to that command!");
			return true;
		}

		else {
			List<String> largs = Arrays.asList(args);
			String Command = largs.get(0);
			largs = largs.subList(1, largs.size());
			(subcommands.get(args[0].toLowerCase())).execute(sender, Command, (largs.size() > 0 ? largs.subList(0, largs.size()) : new ArrayList<String>()));
		}
		return true;
	}

	protected void print(String cmd, String values, String message) {
		if (player.hasPermission("XcraftRegionMarket." + permNodes.get(cmd)))
			sender.sendMessage(ChatColor.DARK_GRAY + "-->" + ChatColor.GREEN + "/rm " + cmd + " " + values + ChatColor.DARK_AQUA + "- " + message);
	}

	public void PrintHelp() {
		sender.sendMessage(ChatColor.BLUE + "["
				+ plugin.getDescription().getFullName() + "] by INemesisI");
		print("confirm", "", "Bestätigt das Kaufen eines Schildes");

		print("creategp", "<id> <preis>", "Erstellt einen neuen GlobalPrice");
		print("setgp", "<id> <preis>", "Ändert den Preis eines GlobalPrice");
		print("listgp", "", "Erstellt einen neuen GlobalPrice");

		print("sell", "<preis>", "Bestätigt das verkaufen eines Schildes");
		print("stop", "", "Stopt den Verkauf eines Schildes");

		print("addplayer", "<name>", "Fügt jmd. zu deiner Region hinzu");
		print("removeplayer", "<name>", "Entfernt jmd. von deiner Region");

		print("settype", "<Typ>", "Ändert den Typ");
		print("setregion", "<region>", "Ändert die Region");
		print("setprice", "<preis>", "Ändert den Preis");
		print("setowner", "<name>", "Ändert den Besitzer");
		print("setintervall", "<intervall> (1d;1h)", "Ändert das Intervall");
		print("setrenter", "<name>", "Ändert den Vermieter");

		print("save", "", "Speichert die Daten");
		print("reload", "", "Lädt das Plugin neu");
	}

	@Override
	public void execute(CommandSender sender, String Command, List<String> list) {
	}
}
