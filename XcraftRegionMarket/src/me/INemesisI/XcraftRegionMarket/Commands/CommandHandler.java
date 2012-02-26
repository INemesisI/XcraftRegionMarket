package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;
import me.INemesisI.XcraftRegionMarket.Commands.ConfirmCommand;
import me.INemesisI.XcraftRegionMarket.Commands.EditCommands;
import me.INemesisI.XcraftRegionMarket.Commands.GPCommands;
import me.INemesisI.XcraftRegionMarket.Commands.PlayerCommands;
import me.INemesisI.XcraftRegionMarket.Commands.PluginCommands;
import me.INemesisI.XcraftRegionMarket.Commands.SellCommand;
import me.INemesisI.XcraftRegionMarket.Commands.StopCommand;

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
		CommandHelper ch = new PluginCommands(plugin);
		addCommand("save", "Save", ch);
		addCommand("reload", "Reload", ch);
		
		addCommand("confirm", "Buy", new ConfirmCommand(plugin));
		addCommand("sell", "Sell", new SellCommand(plugin));
		addCommand("stop", "Sell", new StopCommand(plugin));
		addCommand("dispose", "Dispose", new DisposeCommand(plugin));
		
		ch = new PlayerCommands(plugin);
		addCommand("addplayer", "AddPlayer", ch);
		addCommand("removeplayer", "RemovePlayer", ch);
		
		ch = new GPCommands(plugin);
		addCommand("creategp", "GP.Create", ch);
		addCommand("setgp", "GP.Edit", ch);
		addCommand("listgp", "GP.List", ch);
		
		ch = new EditCommands(plugin);
		addCommand("settype", "Edit.Type", ch);
		addCommand("setregion", "Edit.Region", ch);
		addCommand("setprice", "Edit.Price", ch);
		addCommand("setowner", "Edit.Account", ch);
		addCommand("setintervall", "Edit.Intervall", ch);
		addCommand("setrenter", "Edit.Renter", ch);
		
	}
	
	private void addCommand(String command, String permode, CommandHelper commandclass) {
		permNodes.put(command, permode);
		subcommands.put(command, commandclass);
	}


	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		this.sender = sender;
		player = (sender instanceof Player) ? (Player) sender : null;

		if (player == null) {
			// TODO: Console Stuff
		} else if (args.length == 0 || args[0].equals("help")) {
			PrintHelp(cmd.getName());
			return true;
		} else if (subcommands.get(args[0].toLowerCase()) == null) {
			error("Unkown command: " + args[0].toLowerCase());
		} else if (!(permNodes.get(args[0]).isEmpty() || player.hasPermission(plugin.getDescription().getName() + "." + permNodes.get(args[0])))) {
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

	protected void print(String cmd, String command, String args, String message) {
		if (sender.hasPermission(plugin.getDescription().getName() + "." + permNodes.get(command))) sender
				.sendMessage(ChatColor.DARK_GRAY + "-->" + ChatColor.GREEN + "/" + cmd + " " + command + " " + args + ChatColor.DARK_AQUA + " - " + message);
	}

	public void PrintHelp(String cmd) {
		sender.sendMessage(ChatColor.BLUE + "["
				+ plugin.getDescription().getFullName() + "] by INemesisI");
		print(cmd, "confirm", "", "Best�tigt das Kaufen eines Schildes");
		print(cmd, "sell", "<preis>", "Best�tigt das verkaufen eines Schildes");
		print(cmd, "stop", "", "Stopt den Verkauf eines Schildes");
		print(cmd, "dispose", "", "Verkauft die Region billiger an den Server");
		
		print(cmd, "addplayer", "<name>", "Fügt jmd. zu deiner Region hinzu");
		print(cmd, "removeplayer", "<name>", "Entfernt jmd. von deiner Region");

		print(cmd, "creategp", "<id> <preis>", "Erstellt einen neuen GlobalPrice");
		print(cmd, "setgp", "<id> <preis>", "Ändert den Preis eines GlobalPrice");
		print(cmd, "listgp", "", "Erstellt einen neuen GlobalPrice");

		print(cmd, "settype", "<Typ>", "Ändert den Typ");
		print(cmd, "setregion", "<region>", "Ändert die Region");
		print(cmd, "setprice", "<preis>", "Ändert den Preis");
		print(cmd, "setowner", "<name>", "Ändert den Besitzer");
		print(cmd, "setintervall", "<intervall> (1d;1h)", "Ändert das Intervall");
		print(cmd, "setrenter", "<name>", "Ändert den Vermieter");

		print(cmd, "save", "", "Speichert die Daten");
		print(cmd, "reload", "", "Lädt das Plugin neu");
	}

	@Override
	public void execute(CommandSender sender, String Command, List<String> list) {
	}
}
