package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;
import me.INemesisI.XcraftRegionMarket.Handler.ConfigHandler;
import me.INemesisI.XcraftRegionMarket.Handler.MarketHandler;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public abstract class CommandHelper {
	protected XcraftRegionMarket plugin = null;
	protected CommandSender sender = null;
	protected Player player = null;
	protected WorldGuardPlugin worldguard = null;
	protected Economy economy = null;

	protected CommandHelper(XcraftRegionMarket instance) {
		plugin = instance;
		worldguard = plugin.getWorldguard();
		economy = plugin.getEconomy();
	}

	protected void init(CommandSender sender) {
		this.sender = sender;
		player = (Player) sender;
	}

	protected void reply(String message) {
		sender.sendMessage(plugin.getCName() + message);
	}

	protected void error(String message) {
		sender.sendMessage(ChatColor.RED + "Error: " + message);
	}

	protected MarketHandler getMarketHandler() {
		return plugin.marketHandler;
	}

	protected ConfigHandler getConfigHandler() {
		return plugin.configHandler;
	}

	protected Map<String, MarketSign> getClicked() {
		return plugin.clicked;
	}

	protected Globalprice getGP(String id) {
		return plugin.marketHandler.getGlobalPrice(id);
	}

	protected ArrayList<Globalprice> getGP() {
		return plugin.marketHandler.getGlobalPrices();
	}

	protected void addGP(String id, int price) {
		plugin.marketHandler.addGlobalPrice(new Globalprice(id, price));
	}

	protected boolean delGP(String id) {
		return plugin.marketHandler.removeGlobalPrice(plugin.marketHandler.getGlobalPrice(id));
	}

	protected abstract void execute(CommandSender sender, String Command, List<String> list);
}