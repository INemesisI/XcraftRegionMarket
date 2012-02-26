package me.INemesisI.XcraftRegionMarket.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.Rent;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class MarketHandler {
	private XcraftRegionMarket plugin;
	private ArrayList<MarketSign> signs = new ArrayList<MarketSign>();
	private ArrayList<Globalprice> globalprices = new ArrayList<Globalprice>();
	private Map<String, ArrayList<String>> layouts = new HashMap<String, ArrayList<String>>();

	public MarketHandler(XcraftRegionMarket instance) {
		plugin = instance;
	}

	public ArrayList<String> update(MarketSign ms) {
		ArrayList<String> lines = getLines(ms);
		Sign sign = (Sign) ms.getBlock().getState();
		for (int i = 0; i < 4; i++) {
			sign.setLine(i, lines.get(i));
		}
		sign.update();
		return lines;
	}

	public void add(MarketSign sign) {
		signs.add(sign);
	}

	public boolean remove(MarketSign sign) {
		return signs.remove(sign);
	}

	public MarketSign getMarketSign(Block block) {
		for (MarketSign sign : signs) {
			if (sign.getBlock().equals(block))
				return sign;
		}
		return null;
	}

	public ArrayList<MarketSign> getMarketSigns() {
		return signs;
	}

	public ArrayList<MarketSign> getAllMarketSigns() {
		ArrayList<MarketSign> list = new ArrayList<MarketSign>();
		for (MarketSign ms : signs)
			list.add(ms);
		for (Rent rent : plugin.rentHandler.getRents()) {
			list.add(new MarketSign(rent.getBlock(), rent.getRegion(), rent.getType(), rent.getOwner(), rent.getPrice(), rent.getIntervall()));
		}
		return signs;
	}

	public void setMarketSigns(ArrayList<MarketSign> signs) {
		this.signs = signs;
	}

	public void add(Globalprice GP) {
		globalprices.add(GP);
	}

	public boolean remove(Globalprice GP) {
		return globalprices.remove(GP);
	}

	public Globalprice getGlobalPrice(String id) {
		for (Globalprice gid : globalprices) {
			if (gid.getID().equals(id))
				return gid;
		}
		return null;
	}

	public Globalprice getGlobalPrice(MarketSign ms) {
		for (Globalprice gid : globalprices) {
			if (gid.getMarketSigns().contains(ms))
				return gid;
		}
		return null;
	}
	
	public ArrayList<Globalprice> getGlobalPrices() {
		return globalprices;
	}

	public void setGlobalPrices(ArrayList<Globalprice> globalprices) {
		this.globalprices = globalprices;
	}

	public Map<String, ArrayList<String>> getLayout() {
		return layouts;
	}

	public void setLayout(Map<String, ArrayList<String>> layout) {
		this.layouts = layout;
	}

	private ArrayList<String> getLines(MarketSign sign) {
		ArrayList<String> layout = layouts.get(sign.getType());
		ArrayList<String> repl = new ArrayList<String>();
		repl.ensureCapacity(3);
		String intervall = sign.getIntervall();
		String region = sign.getRegion();
		// format intervall
		if (!intervall.isEmpty()) {
			String[] split = intervall.split(" & ");
			if (split[0].substring(0, 1).equals("0"))
				intervall = split[1];
			if (split[1].substring(0, 1).equals("0"))
				intervall = split[0];
		}
		// format region
		Map<String, String> format = plugin.configHandler.getRegionformat();
		region = Character.toUpperCase(region.charAt(0))
				+ region.substring(1, region.length());
		for (String key : format.keySet()) {
			region = region.replace(key, format.get(key));
		}
		for (String line : layout) {
			line = line.replaceAll("&([a-f0-9])", "\u00A7$1");
			line = line.replace("<region>", region);
			line = line.replace("<account>", "" + sign.getOwner());
			line = line.replace("<price>",
					"" + plugin.getEconomy().format(sign.getPrice()));
			line = line.replace("<intervall>", intervall);
			repl.add(line);
		}
		return repl;
	}
}
