package me.INemesisI.XcraftRegionMarket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import me.INemesisI.XcraftRegionMarket.Commands.CommandHandler;
import me.INemesisI.XcraftRegionMarket.Handler.ConfigHandler;
import me.INemesisI.XcraftRegionMarket.Handler.GroupHandler;
import me.INemesisI.XcraftRegionMarket.Handler.MarketHandler;
import me.INemesisI.XcraftRegionMarket.Handler.RegionHandler;
import me.INemesisI.XcraftRegionMarket.Handler.RentHandler;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;
import com.platymuus.bukkit.permissions.PermissionsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class XcraftRegionMarket extends JavaPlugin {

	private final EventListener eventlistener = new EventListener(this);
	public MarketHandler marketHandler;
	public RentHandler rentHandler;
	public ConfigHandler configHandler;
	public RegionHandler regionHandler;
	public GroupHandler groupHandler;
	//public TaxHandler taxHandler;

	private WorldGuardPlugin worldguard;
	private PermissionsPlugin permission = null;
	private Method economy = null;
	public Map<String, MarketSign> clicked = new HashMap<String, MarketSign>();

	public Logger log = Logger.getLogger("Minecraft");

	@Override
	public void onDisable() {
		configHandler.save();
		log.info(this.getDescription().getName() + "disabled!");
	}

	@Override
	public void onEnable() {
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(eventlistener, this);

		getCommand("rm").setExecutor(new CommandHandler(this));

		setupEconomy();
		setupPermissions();
		setupWorldguard();
		setupHandler();
		startScheduler();

		log.info(this.getDescription().getName() + " enabled!");
	}


	private Boolean setupPermissions() {
		permission = (PermissionsPlugin) getServer().getPluginManager().getPlugin("PermissionsBukkit");
		return (permission != null);
	}

	private Boolean setupEconomy() {
		economy = Methods.getMethod();
		return economy != null;
	}

	private Boolean setupWorldguard() {
		worldguard = (WorldGuardPlugin) this.getServer().getPluginManager().getPlugin("WorldGuard");
		return (worldguard != null);
	}
	
	private void setupHandler() {
		marketHandler = new MarketHandler(this);
		rentHandler = new RentHandler(this);
		//taxHandler = new TaxHandler(this);
		configHandler = new ConfigHandler(this);
		regionHandler = new RegionHandler(this);
		groupHandler = new GroupHandler(this);
		
		configHandler.load();
	}

	public Method getEconomy() {
		return economy;
	}

	public PermissionsPlugin getPermission() {
		return permission;
	}

	public WorldGuardPlugin getWorldguard() {
		return worldguard;
	}
	
	public void startScheduler() {
		SimpleDateFormat d = new SimpleDateFormat();
		d.applyPattern("mm:ss");
		String current = d.format(new Date());
		String[] split = current.split(":");
		int min = Integer.parseInt(split[0]);
		int sec = Integer.parseInt(split[1]);

		min = 60 - min;
		sec = 60 - sec;
		if (min != 0)
			min--;
		Runnable task = new Runnable() {
			@Override
			public void run() {
				rentHandler.checkRents();
				//taxHandler.checkTaxes();
			}
		};
		getServer().getScheduler().scheduleSyncRepeatingTask(this, task, (min * 60 + sec) * 20, 72000);

	}

	public String getName() {
		return ChatColor.DARK_GRAY + "[" + this.getDescription().getName() + "] " + getChatColor();
	}

	public ChatColor getChatColor() {
		return ChatColor.DARK_AQUA;
	}
	
	public void Debug(String s) {
		if (configHandler.isDebug())
		log.info(this.getDescription().getName()+" DEBUG: "+s);
	}
}