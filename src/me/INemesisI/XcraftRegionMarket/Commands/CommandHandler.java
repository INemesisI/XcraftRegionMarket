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
		CommandHelper ch = new PluginCommands(plugin);
		this.addCommand("save", "Save", ch);
		this.addCommand("reload", "Reload", ch);

		this.addCommand("confirm", "Buy", new ConfirmCommand(plugin));
		this.addCommand("sell", "Sell", new SellCommand(plugin));
		this.addCommand("stop", "Sell", new StopCommand(plugin));
		this.addCommand("dispose", "Dispose", new DisposeCommand(plugin));

		ch = new PlayerCommands(plugin);
		this.addCommand("addplayer", "AddPlayer", ch);
		this.addCommand("removeplayer", "RemovePlayer", ch);

		ch = new GPCommands(plugin);
		this.addCommand("creategp", "GP.Create", ch);
		this.addCommand("setgp", "GP.Edit", ch);
		this.addCommand("listgp", "GP.List", ch);

		ch = new EditCommands(plugin);
		this.addCommand("settype", "Edit.Type", ch);
		this.addCommand("setregion", "Edit.Region", ch);
		this.addCommand("setprice", "Edit.Price", ch);
		this.addCommand("setowner", "Edit.Account", ch);
		this.addCommand("setintervall", "Edit.Intervall", ch);
		this.addCommand("setrenter", "Edit.Renter", ch);

		this.addCommand("limit", "Edit.Limit", new LimitCommands(plugin));

	}

	private void addCommand(String command, String permode, CommandHelper commandclass) {
		permNodes.put(command, permode);
		subcommands.put(command, commandclass);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		this.sender = sender;
		player = sender instanceof Player ? (Player) sender : null;

		if (player == null) {
			// TODO: Console Stuff
		} else if ((args.length == 0) || args[0].equals("help")) {
			this.PrintHelp(cmd.getName());
			return true;
		} else if (subcommands.get(args[0].toLowerCase()) == null) {
			this.error("Unkown command: " + args[0].toLowerCase());
		} else if (!(permNodes.get(args[0]).isEmpty() || player.hasPermission(plugin.getDescription().getName() + "."
				+ permNodes.get(args[0])))) {
			this.error("You do not have access to that command!");
			return true;
		}

		else {
			List<String> largs = Arrays.asList(args);
			String Command = largs.get(0);
			largs = largs.subList(1, largs.size());
			subcommands.get(args[0].toLowerCase()).execute(sender, Command,
					largs.size() > 0 ? largs.subList(0, largs.size()) : new ArrayList<String>());
		}
		return true;
	}

	protected void print(String cmd, String command, String args, String message) {
		if (sender.hasPermission(plugin.getDescription().getName() + "." + permNodes.get(command))) {
			sender.sendMessage(ChatColor.DARK_GRAY + "-->" + ChatColor.GREEN + "/" + cmd + " " + command + " " + args
					+ ChatColor.DARK_AQUA + " - " + message);
		}
	}

	public void PrintHelp(String cmd) {
		sender.sendMessage(ChatColor.BLUE + "[" + plugin.getDescription().getFullName() + "] by INemesisI");
		this.print(cmd, "confirm", "", "Bestätigt das Kaufen einer Region");
		this.print(cmd, "sell", "<preis>", "Bestätigt das verkaufen einer Region");
		this.print(cmd, "stop", "", "Stopt den Verkauf einer Region");
		this.print(cmd, "dispose", "", "Verkauft eine Region an den Server");

		this.print(cmd, "addplayer", "<name>", "Fügt jmd. zu einer Region hinzu");
		this.print(cmd, "removeplayer", "<name>", "Entfernt jmd. von einer Region");

		this.print(cmd, "creategp", "<id> <preis>", "Erstellt einen GlobalPrice");
		this.print(cmd, "setgp", "<id> <preis>", "Ändert den Preis eines GlobalPrice");
		this.print(cmd, "listgp", "", "Erstellt einen neuen GlobalPrice");

		this.print(cmd, "settype", "<Typ>", "Ändert den Typ");
		this.print(cmd, "setregion", "<region>", "Ändert die Region");
		this.print(cmd, "setprice", "<preis>", "Ändert den Preis");
		this.print(cmd, "setowner", "<name>", "Ändert den Besitzer");
		this.print(cmd, "setintervall", "<intervall> (1d;1h)", "Ändert das Intervall");
		this.print(cmd, "setrenter", "<name>", "Ändert den Vermieter");

		this.print(cmd, "save", "", "Speichert die Daten");
		this.print(cmd, "reload", "", "Lädt das Plugin neu");
	}

	@Override
	public void execute(CommandSender sender, String Command, List<String> list) {
	}
}
