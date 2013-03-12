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
import me.INemesisI.XcraftRegionMarket.MarketSign.Type;
import me.INemesisI.XcraftRegionMarket.RentSign;
import me.INemesisI.XcraftRegionMarket.SellSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

// Daten- und Configverwaltung

public class ConfigHandler {
	private XcraftRegionMarket plugin;
	private MarketHandler mh;

	private FileConfiguration config;
	private FileConfiguration datacfg;
	private File dataFile;

	private String ServerAccount;

	private Map<String, Integer> selllimit = new HashMap<String, Integer>(); // type,limit
	private Map<String, Integer> rentlimit = new HashMap<String, Integer>(); // type,limit

	private Map<String, ArrayList<String>> sellgroups = new HashMap<String, ArrayList<String>>(); // group,regions
	private Map<String, ArrayList<String>> rentgroups = new HashMap<String, ArrayList<String>>(); // group,regions
	private ArrayList<String> ignoredgroups = new ArrayList<String>();

	private Map<String, String> regionformat = new HashMap<String, String>();

	private SimpleDateFormat date = new SimpleDateFormat();

	private boolean debug;

	private int dispose;
	private int max;

	public ConfigHandler(XcraftRegionMarket instance) {
		plugin = instance;
		mh = plugin.marketHandler;
		date.applyPattern("yyyy.MM.dd HH:mm");
	}

	public void load() {
		config = plugin.getConfig();

		this.setDefaults();
		ConfigurationSection cs;
		// Configload

		this.setServerAccount(config.getString("ServerAccount"));

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
		for (String group : config.getConfigurationSection("GroupManager.sell").getKeys(false)) {
			ArrayList<String> list = new ArrayList<String>();
			for (Object region : config.getList("GroupManager.sell." + group)) {
				String r = (String) region;
				list.add(r);
			}
			sellgroups.put(group, list);
		}
		for (String group : config.getConfigurationSection("GroupManager.rent").getKeys(false)) {
			ArrayList<String> list = new ArrayList<String>();
			for (Object region : config.getList("GroupManager.rent." + group)) {
				String r = (String) region;
				list.add(r);
			}
			sellgroups.put(group, list);
		}
		// Regionformat
		for (String key : config.getConfigurationSection("Format.Region").getKeys(false)) {
			regionformat.put(key, config.getString("Format.Region." + key));
		}
		// Layout load
		Map<String, ArrayList<String>> layout = new HashMap<String, ArrayList<String>>();
		cs = config.getConfigurationSection("Layout");
		for (String key : cs.getKeys(false)) {
			layout.put(key, (ArrayList<String>) cs.getStringList(key));
		}
		mh.setLayout(layout);

		// Debug
		this.setDebug(config.getBoolean("Debug", false));
		// Dispose
		this.setDispose(config.getInt("Dispose.percentage", 50));
		this.setMax(config.getInt("Dispose.max", 0));

		dataFile = new File(plugin.getDataFolder(), "Data.yml");
		if (!dataFile.exists()) {
			return;
		}
		datacfg = YamlConfiguration.loadConfiguration(dataFile);
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		// GlobalPrice load
		cs = datacfg.getConfigurationSection("GlobalPrices");
		for (String key : cs.getKeys(false)) {
			mh.addGlobalPrice(new Globalprice(key, cs.getInt(key)));
		}

		// Signload
		cs = datacfg.getConfigurationSection("MarketSigns");
		if (cs != null) {
			for (String worldkey : cs.getKeys(false)) {
				ConfigurationSection regioncs = datacfg.getConfigurationSection("MarketSigns." + worldkey);
				World world = plugin.getServer().getWorld(worldkey);
				if (world == null) {
					continue;
				}

				for (String regionkey : regioncs.getKeys(false)) {
					ProtectedRegion region = plugin.regionHandler.getRegion(world, regionkey);
					if (region != null) {
						this.loadSign(world, region);
					} else {
						plugin.log.warning(plugin.getCName() + "Could not find region " + regionkey + " in world "
								+ worldkey + ". The corresponding MarketSign was not loaded!");
					}
				}
			}
		}
	}

	public void save() {
		if (datacfg != null) {
			for (String key : datacfg.getKeys(false)) {
				datacfg.set(key, null);
			}
		}
		for (SellSign sign : mh.getSellSigns()) {
			Block b = sign.getBlock();
			String key = "SellSigns." + b.getWorld().getName() + "." + sign.getRegion().getId();
			datacfg.set(key + ".Type", sign.getType());
			datacfg.set(key + ".Account", sign.getOwner());
			datacfg.set(key + ".Price", sign.getPrice());
			datacfg.set(key + ".GP", sign.getGp().getID());
			datacfg.set(key + ".X", b.getX());
			datacfg.set(key + ".Y", b.getY());
			datacfg.set(key + ".Z", b.getZ());
		}
		for (RentSign sign : mh.getRentSigns()) {
			Block b = sign.getBlock();
			String key = "RentSigns." + b.getWorld().getName() + "." + sign.getRegion().getId();
			datacfg.set(key + ".Type", sign.getType());
			datacfg.set(key + ".Account", sign.getOwner());
			datacfg.set(key + ".Price", sign.getPrice());
			datacfg.set(key + ".Renter", sign.getRenter());
			datacfg.set(key + ".GP", sign.getGp().getID());
			datacfg.set(key + ".Intervall", sign.getIntervall());
			datacfg.set(key + ".Paytime", date.format(sign.getPaytime()));
			datacfg.set(key + ".X", b.getX());
			datacfg.set(key + ".Y", b.getY());
			datacfg.set(key + ".Z", b.getZ());
		}
		for (Globalprice gp : mh.getGlobalPrices()) {
			datacfg.set("GlobalPrices." + gp.getID(), gp.getPrice());
		}
		try {
			datacfg.save(dataFile);
		} catch (IOException e) {
		};
	}

	public boolean loadSign(World world, ProtectedRegion region) {
		ConfigurationSection sign = datacfg.getConfigurationSection("SellSigns." + world.getName() + "." + region);
		if (sign == null) {
			sign = datacfg.getConfigurationSection("RentSigns." + world.getName() + "." + region);
		}
		Block block = world.getBlockAt(sign.getInt("X"), sign.getInt("Y"), sign.getInt("Z"));
		if (block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN)) {
			Type type = Type.valueOf(sign.getString("Type"));
			if ((type == Type.RENTING) || (type == Type.RENTED)) { // [rented]
				Date paytime = null;
				try {
					paytime = date.parse(sign.getString("Paytime"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				mh.add(new RentSign(block, region, type, sign.getString("Account"), sign.getDouble("Price"), mh
						.getGlobalPrice(sign.getString("GP")), sign.getString("Renter"), sign.getString("Intervall"),
						paytime));
			} else if ((type == Type.SELLING) || (type == Type.SOLD)) {
				mh.add(new SellSign(block, region, type, sign.getString("Account"), sign.getDouble("Price"), mh
						.getGlobalPrice(sign.getString("GP"))));
			}
		} else {
			plugin.log.warning(plugin.getCName() + "Could not find a sign block for region " + region
					+ ". The marketsign has not been loaded!");
			return false;
		}
		return true;
	}

	private void setDefaults() {
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

	public String getServerAccount() {
		return ServerAccount;
	}

	public void setServerAccount(String serverAccount) {
		ServerAccount = serverAccount;
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

	public double getDispose(double price) {
		double p = ((double) this.getDispose() / 100) * price;
		if ((this.getMax() <= 0) && (p > this.getMax())) {
			p = this.getMax();
		}
		return p;
	}

	public int getDispose() {
		return dispose;
	}

	public void setDispose(int dispose) {
		this.dispose = dispose;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public Map<String, ArrayList<String>> getRentgroups() {
		return rentgroups;
	}

	public void setRentgroups(Map<String, ArrayList<String>> rentgroups) {
		this.rentgroups = rentgroups;
	}

	public Map<String, ArrayList<String>> getSellgroups() {
		return sellgroups;
	}

	public void setSellgroups(Map<String, ArrayList<String>> sellgroups) {
		this.sellgroups = sellgroups;
	}
}