package me.INemesisI.XcraftRegionMarket.Handler;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.Rent;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

// Daten- und Configverwaltung

public class ConfigHandler {
	private XcraftRegionMarket plugin;
	private MarketHandler mh;

	private FileConfiguration config;
	private FileConfiguration datacfg;
	private File dataFile;

	private Map<String, Integer> selllimit = new HashMap<String, Integer>();
	private Map<String, Integer> rentlimit = new HashMap<String, Integer>();
	private Map<Integer, String> sellgroups = new HashMap<Integer, String>();

	private Map<Integer, String> rentgroups = new HashMap<Integer, String>();
	private Map<String, String> regionformat = new HashMap<String, String>();

	private ArrayList<String> ignoredgroups = new ArrayList<String>();
	private SimpleDateFormat date = new SimpleDateFormat();
	
	private boolean debug;

	public ConfigHandler(XcraftRegionMarket instance) {
		plugin = instance;
		mh = plugin.marketHandler;
		date.applyPattern("yyyy.MM.dd HH:mm");
	}

	@SuppressWarnings("unchecked")
	public void load() {
		config = plugin.getConfig();
		
		setDefaults();
		ConfigurationSection cs;
		// Configload
		selllimit.put("global", config.getInt("Limits.sell.global", -1));
		for (String key : config.getConfigurationSection("Limits.sell.worlds").getKeys(false)) {
			selllimit.put("w:" + key, config.getInt("Limits.sell.worlds." + key));
		}
		for (String key : config.getConfigurationSection("Limits.sell.groups").getKeys(false)) {
			selllimit.put("g:" + key, config.getInt("Limits.sell.groups." + key));
		}
		for (String key : config.getConfigurationSection("Limits.sell.parents").getKeys(false)) {
			selllimit.put("p:" + key, config.getInt("Limits.sell.parents." + key));
		}

		rentlimit.put("global", config.getInt("Limits.rent.global", -1));
		for (String key : config.getConfigurationSection("Limits.rent.worlds").getKeys(false)) {
			rentlimit.put("w:" + key, config.getInt("Limits.rent.worlds." + key));
		}
		for (String key : config.getConfigurationSection("Limits.rent.groups").getKeys(false)) {
			rentlimit.put("g:" + key, config.getInt("Limits.rent.groups." + key));
		}
		for (String key : config.getConfigurationSection("Limits.rent.parents").getKeys(false)) {
			selllimit.put("p:" + key, config.getInt("Limits.rent.parents." + key));
		}
		// Groupmanager
		ignoredgroups.addAll(Arrays.asList(config.getString("GroupManager.ignore").split(", ")));
		for (String num : config.getConfigurationSection("GroupManager.sell").getKeys(false)) {
			sellgroups.put(Integer.parseInt(num), config.getString("GroupManager.sell." + num));
		}
		for (String num : config.getConfigurationSection("GroupManager.rent").getKeys(false)) {
			rentgroups.put(Integer.parseInt(num), config.getString("GroupManager.rent." + num));
		}
		// Regionformat
		for (String key : config.getConfigurationSection("Format.Region").getKeys(false)) {
			regionformat.put(key, config.getString("Format.Region." + key));
		}
		// Layout load
		Map<String, ArrayList<String>> layout = new HashMap<String, ArrayList<String>>();
		cs = config.getConfigurationSection("Layout");
		for (String key : cs.getKeys(false))
			layout.put(key, (ArrayList<String>) cs.getList(key));
		mh.setLayout(layout);
		// Debug
		setDebug(config.getBoolean("Debug", false));
		
		dataFile = new File(plugin.getDataFolder(), "Data.yml");
		if (!dataFile.exists()) {
			loadold();
			return;
		}
		datacfg = YamlConfiguration.loadConfiguration(dataFile);
		// Tax
		/*
		plugin.taxHandler.setOfflinetime(config.getLong("Tax.Offlinetime"));
		plugin.taxHandler.setPercentage(config.getDouble("Tax.Percentage", 1));
		plugin.taxHandler.setIntervall(config.getInt("Tax.Intervall", 1));
		Date d = null;
		String time = (datacfg.getString("Tax.Date"));
		if (time != null)
		try {
			d = date.parse(time);
		} catch (ParseException e1) {}
		plugin.taxHandler.setDate(d);
		*/
		// Signload
		ArrayList<MarketSign> signs = new ArrayList<MarketSign>();
		ArrayList<Rent> rents = new ArrayList<Rent>();
		plugin.rentHandler.setRents(new ArrayList<Rent>());
		cs = datacfg.getConfigurationSection("MarketSigns");
		if (cs != null)
		for (String worldkey : cs.getKeys(false)) {
			ConfigurationSection rcs = datacfg.getConfigurationSection("MarketSigns." + worldkey);
			for (String regionkey : rcs.getKeys(false)) {
				World world = plugin.getServer().getWorld(worldkey);
				ConfigurationSection sign = datacfg.getConfigurationSection("MarketSigns." + worldkey + "." + regionkey);
				Block block = world.getBlockAt(sign.getInt("X"), sign.getInt("Y"), sign.getInt("Z"));
				if ((block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN))) {
					String type = sign.getString("Type");
					if (type.equals("sell") || type.equals("sold"))
						signs.add(new MarketSign(block, regionkey, type, sign.getString("Account"), sign.getDouble("Price")));
					if (type.equals("rent"))
						signs.add(new MarketSign(block, regionkey, type, sign.getString("Account"), sign.getDouble("Price"), sign.getString("Intervall")));

					if (type.equals("rented")) {
						Date paytime = null;
						try {
							paytime = date.parse(sign.getString("Paytime"));
						} catch (ParseException e) {
						}
						Rent rent = new Rent(block, regionkey, sign.getString("Account"), sign.getDouble("Price"), sign.getString("Intervall"));
						rent.setPaytime(paytime);
						rent.setRenter(sign.getString("Renter"));
						rents.add(rent);
					}
				} else {
					datacfg.set("MarketSigns." + worldkey + "." + regionkey, null);
				}
			}
		}
		mh.setMarketSigns(signs);
		plugin.rentHandler.setRents(rents);
		// GlobalPrice load
		ArrayList<Globalprice> gps = new ArrayList<Globalprice>();
		cs = datacfg.getConfigurationSection("GlobalPrices");
		if (cs != null)
		for (String key : cs.getKeys(false)) {
			ArrayList<MarketSign> ms = new ArrayList<MarketSign>();
			for (String region : (ArrayList<String>) cs.get(key + ".MarketSigns")) {
				for (MarketSign sign : mh.getMarketSigns()) {
					if (sign.getRegion().equals(region))
						ms.add(sign);
				}
			}
			gps.add(new Globalprice(key, cs.getInt(key + ".Price"), ms));
		}
		mh.setGlobalPrices(gps);

		// update Sign text
		for (Globalprice gpr : mh.getGlobalPrices()) {
			for (MarketSign ms : gpr.getMarketSigns()) {
				ms.setPrice(gpr.getPrice(plugin.regionHandler.getRegion(ms)));
			}
			for (Rent rent : gpr.getRents()) {
				rent.setPrice(gpr.getPrice(plugin.regionHandler.getRegion(rent)));
			}
		}
		for (MarketSign ms : mh.getMarketSigns()) {
			mh.update(ms);
		}
		for (Rent rent : plugin.rentHandler.getRents()) {
			mh.update(rent);
		}

		try {
			datacfg.save(dataFile);
		} catch (IOException e) {
		}
	}

	public void save() {
		for (String key : datacfg.getKeys(false))
			datacfg.set(key, null);
		for (MarketSign sign : mh.getMarketSigns()) {
			Block b = sign.getBlock();
			String key = "MarketSigns." + b.getWorld().getName() + "." + sign.getRegion();
			datacfg.set(key + ".Type", sign.getType());
			if (sign.getType().equals("rent"))
				datacfg.set(key + ".Intervall", sign.getIntervall());
			datacfg.set(key + ".Price", sign.getPrice());
			datacfg.set(key + ".Account", sign.getOwner());
			datacfg.set(key + ".X", b.getX());
			datacfg.set(key + ".Y", b.getY());
			datacfg.set(key + ".Z", b.getZ());
		}
		for (Rent rent : plugin.rentHandler.getRents()) {
			Block b = rent.getBlock();
			String key = "MarketSigns." + b.getWorld().getName() + "." + rent.getRegion();
			datacfg.set(key + ".Type", rent.getType());
			datacfg.set(key + ".Intervall", rent.getIntervall());
			datacfg.set(key + ".Paytime", date.format(rent.getPaytime()));
			datacfg.set(key + ".Renter", rent.getRenter());
			datacfg.set(key + ".Price", rent.getPrice());
			datacfg.set(key + ".Account", rent.getOwner());
			datacfg.set(key + ".X", b.getX());
			datacfg.set(key + ".Y", b.getY());
			datacfg.set(key + ".Z", b.getZ());
		}
		for (Globalprice gp : mh.getGlobalPrices()) {
			datacfg.set("GlobalPrices." + gp.getID() + ".Price", gp.getPrice());
			ArrayList<String> list = new ArrayList<String>();
			for (MarketSign s : gp.getMarketSigns()) {
				list.add(s.getRegion());
			}
			for (Rent r : plugin.rentHandler.getRents()) {
				list.add(r.getRegion());
			}
			datacfg.set("GlobalPrices." + gp.getID() + ".MarketSigns", list);
		}
		try {
			datacfg.save(dataFile);
		} catch (IOException e) {};
	}
	
	@SuppressWarnings("unchecked")
	private void loadold() {
		File gpFile = new File(plugin.getDataFolder(), "GlobalPrices.yml");
		File msFile = new File(plugin.getDataFolder(), "MarketSigns.yml");
		FileConfiguration gpcfg = YamlConfiguration.loadConfiguration(gpFile);
		FileConfiguration mscfg = YamlConfiguration.loadConfiguration(msFile);
		ConfigurationSection cs;
		// Signload
		ArrayList<MarketSign> signs = new ArrayList<MarketSign>();
		ArrayList<Rent> rents = new ArrayList<Rent>();
		plugin.rentHandler.setRents(new ArrayList<Rent>());
		for (String worldkey : mscfg.getKeys(false)) {
			cs = mscfg.getConfigurationSection(worldkey);
			for (String regionkey : cs.getKeys(false)) {
				World world = plugin.getServer().getWorld(worldkey);
				ConfigurationSection cs2 = mscfg.getConfigurationSection(worldkey + "." + regionkey);
				Block block = world.getBlockAt(cs2.getInt("X"), cs2.getInt("Y"), cs2.getInt("Z"));
					String type = cs2.getString("Type");
					if (type.equals("sell") || type.equals("sold"))
						signs.add(new MarketSign(block, regionkey, type, cs2.getString("Account"), cs2.getDouble("Price")));
					if (type.equals("rent"))
						signs.add(new MarketSign(block, regionkey, type, cs2.getString("Account"), cs2.getDouble("Price"), cs2.getString("Intervall")));

					if (type.equals("rented")) {
						Date paytime = null;
						try {
							paytime = date.parse(cs2.getString("Paytime"));
						} catch (ParseException e) {
						}
						Rent rent = new Rent(block, regionkey, cs2.getString("Account"), cs2.getDouble("Price"), cs2.getString("Intervall"));
						rent.setPaytime(paytime);
						rent.setRenter(cs2.getString("Renter"));
						rents.add(rent);
				}
			}
		}
		mh.setMarketSigns(signs);
		plugin.rentHandler.setRents(rents);
		mscfg = null;
		msFile.delete();
		// GlobalPrice load
		ArrayList<Globalprice> gp = new ArrayList<Globalprice>();
		for (String key : gpcfg.getKeys(false)) {
			ArrayList<MarketSign> ms = new ArrayList<MarketSign>();
			for (String block : (ArrayList<String>) gpcfg.get(key + ".MarketSigns")) {
				for (MarketSign sign : mh.getMarketSigns()) {
					if (sign.getBlock().equals(getBlock(block)))
						ms.add(sign);
				}
			}
			gp.add(new Globalprice(key, gpcfg.getInt(key + ".Price"), ms));
		}
		mh.setGlobalPrices(gp);
		gpcfg = null;
		gpFile.delete();
		datacfg = YamlConfiguration.loadConfiguration(dataFile);
		save();
	}

	private void setDefaults() {
		// TODO: Fix defaults
		config.addDefault("Limits.sell.global", -1);
		config.addDefault("Limits.sell.worlds", "");
		config.addDefault("Limits.sell.groups", "");
		config.addDefault("Limits.rent.global", -1);
		config.addDefault("Limits.sell.worlds", "");
		config.addDefault("Limits.sell.groups", "");
		config.addDefault("GroupManager.1", "");
		List<String> list = new ArrayList<String>();
		list.add("[Zu verkaufen]");
		list.add("<region>");
		list.add("<price>");
		list.add("<account>");
		config.addDefault("Layout.sell", list);
		list.set(0, "[Zu vermieten]");
		list.set(3, "<intervall>");
		config.addDefault("Layout.rent", list);
		list.set(0, "");
		list.set(3, "");
		config.addDefault("Layout.sold", list);
		list.set(0, "[Vermietet]");
		list.set(2, "<account>");
		config.addDefault("Layout.rented", list);
	}

	private Block getBlock(String s) {
		String[] split = s.split(";");
		World world = plugin.getServer().getWorld(split[1]);
		split = split[0].split(",");
		return world.getBlockAt(Integer.parseInt(split[0]),
				Integer.parseInt(split[1]), Integer.parseInt(split[2]));
	}

	public Map<String, Integer> getSelllimit() {
		return selllimit;
	}

	public void setSelllimit(Map<String, Integer> selllimit) {
		this.selllimit = selllimit;
	}

	public Map<String, Integer> getRentlimit() {
		return rentlimit;
	}

	public void setRentlimit(Map<String, Integer> rentlimit) {
		this.rentlimit = rentlimit;
	}

	public Map<Integer, String> getRentgroups() {
		return rentgroups;
	}

	public void setRentgroups(Map<Integer, String> groupmanager) {
		this.rentgroups = groupmanager;
	}
	
	public Map<Integer, String> getSellgroups() {
		return sellgroups;
	}

	public void setSellgroups(Map<Integer, String> sellgroups) {
		this.sellgroups = sellgroups;
	}

	public Map<String, String> getRegionformat() {
		return regionformat;
	}

	public void setRegionformat(Map<String, String> regionformat) {
		this.regionformat = regionformat;
	}

	public ArrayList<String> getIgnoredgroups() {
		return ignoredgroups;
	}

	public void setIgnoredgroups(ArrayList<String> ignoredgroups) {
		this.ignoredgroups = ignoredgroups;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}