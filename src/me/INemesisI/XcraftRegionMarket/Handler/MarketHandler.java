package me.INemesisI.XcraftRegionMarket.Handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import me.INemesisI.XcraftRegionMarket.Globalprice;
import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.RentSign;
import me.INemesisI.XcraftRegionMarket.SellSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class MarketHandler {
	private XcraftRegionMarket plugin;
	private SimpleDateFormat date = new SimpleDateFormat();
	private ArrayList<SellSign> sellsigns = new ArrayList<SellSign>();
	private ArrayList<RentSign> rentsigns = new ArrayList<RentSign>();
	private ArrayList<Globalprice> globalprices = new ArrayList<Globalprice>();
	private Map<String, ArrayList<String>> layouts = new HashMap<String, ArrayList<String>>();

	public MarketHandler(XcraftRegionMarket instance) {
		plugin = instance;
		date.applyPattern("yyyy.MM.dd HH:mm");
	}

	public ArrayList<String> update(MarketSign ms) {
		ms.updatePrice();
		ArrayList<String> lines = this.getFormattedLines(ms);
		Sign sign = (Sign) ms.getBlock().getState();
		for (int i = 0; i < 4; i++) {
			sign.setLine(i, lines.get(i));
		}
		sign.update();
		return lines;
	}

	public void updateAll(Globalprice gp) {
		for (MarketSign sign : sellsigns) {
			if (sign.getGp().equals(gp)) {
				this.update(sign);
			}
		}
		for (MarketSign sign : rentsigns) {
			if (sign.getGp().equals(gp)) {
				this.update(sign);
			}
		}
	}

	public void checkRents() {
		Date currenttime = null;
		try {
			currenttime = date.parse(date.format(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (currenttime != null) {
			for (RentSign rent : rentsigns) {
				this.checkRent(rent, currenttime);
			}
		}
	}

	private void checkRent(RentSign rent, Date currenttime) {
		if (!currenttime.before(rent.getPaytime())) {
			Player player = plugin.getServer().getPlayer(rent.getOwner());
			if (plugin.getEconomy().getBalance(rent.getOwner()) >= rent.getPrice()) {
				plugin.getEconomy().depositPlayer(rent.getRenter(), rent.getPrice());
				plugin.getEconomy().withdrawPlayer(rent.getOwner(), rent.getPrice());
				if (player != null) {
					player.sendMessage(plugin.getCName() + "Dir wurde deine Miete von "
							+ plugin.getEconomy().format(rent.getPrice()) + " abgezogen");
				}
				plugin.Debug("withdrew " + plugin.getEconomy().format(rent.getPrice()) + " from " + rent.getOwner()
						+ " for renting region " + rent.getRegion());
				rent.setNextPaytime(currenttime);
				plugin.Debug("next paytime: " + date.format(rent.getPaytime()));
				this.checkRent(rent, currenttime);
			} else {
				if (player != null) {
					player.sendMessage(plugin.getCName() + "Du hattest nicht genügend Geld um deine Miete für "
							+ rent.getRegion() + " zu bezahlen.");
					player.sendMessage(plugin.getCName() + "Die Rechte für diese Region wurden dir genommen!");
				}
				plugin.Debug(player + " had not enough money to pay his rented region " + rent.getRegion());
				plugin.regionHandler.removeAllPlayers(rent.getRegion());
				plugin.regionHandler.addOwner(rent.getRegion(), rent.getOwner());
				rent.unrent();
			}
		}
	}

	public void add(SellSign sign) {
		sellsigns.add(sign);
	}

	public void add(RentSign sign) {
		rentsigns.add(sign);
	}

	public boolean remove(MarketSign ms) {
		if (ms instanceof SellSign) {
			return sellsigns.remove(ms);
		} else {
			return rentsigns.remove(ms);
		}
	}

	public MarketSign getMarketSign(Block block) {
		for (MarketSign sign : sellsigns) {
			if (sign.getBlock().equals(block)) {
				return sign;
			}
		}
		for (MarketSign sign : rentsigns) {
			if (sign.getBlock().equals(block)) {
				return sign;
			}
		}
		return null;
	}

	public ArrayList<SellSign> getSellSigns() {
		return sellsigns;
	}

	public void setSellSigns(ArrayList<SellSign> signs) {
		sellsigns = signs;
	}

	public ArrayList<RentSign> getRentSigns() {
		return rentsigns;
	}

	public void setRentSigns(ArrayList<RentSign> signs) {
		rentsigns = signs;
	}

	public ArrayList<MarketSign> getAllMarketSigns() {
		ArrayList<MarketSign> list = new ArrayList<MarketSign>();
		list.addAll(sellsigns);
		list.addAll(rentsigns);
		return list;
	}

	public void addGlobalPrice(Globalprice GP) {
		globalprices.add(GP);
	}

	public boolean removeGlobalPrice(Globalprice GP) {
		return globalprices.remove(GP);
	}

	public Globalprice getGlobalPrice(String id) {
		for (Globalprice gp : globalprices) {
			if (gp.getID().equals(id)) {
				return gp;
			}
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
		layouts = layout;
	}

	private ArrayList<String> getFormattedLines(MarketSign sign) {
		ArrayList<String> layout = layouts.get(sign.getType().toString());
		ArrayList<String> repl = new ArrayList<String>(3);
		String region = sign.getRegion().getId();
		String intervall = "";
		// format intervall
		if (sign instanceof RentSign) {
			intervall = ((RentSign) sign).getIntervall();
			String[] split = intervall.split(" & ");
			if (split[0].substring(0, 1).equals("0")) {
				intervall = split[1];
			}
			if (split[1].substring(0, 1).equals("0")) {
				intervall = split[0];
			}
		}
		// format region
		Map<String, String> format = plugin.configHandler.getRegionformat();
		region = Character.toUpperCase(region.charAt(0)) + region.substring(1, region.length());
		for (String key : format.keySet()) {
			region = region.replace(key, format.get(key));
		}
		// format sign
		for (String line : layout) { // TODO: type?!
			line = line.replaceAll("&([a-f0-9])", "\u00A7$1"); // Color code
			line = line.replace("<region>", region);
			line = line.replace("<account>", "" + sign.getOwner());
			line = line.replace("<price>", "" + plugin.getEconomy().format(sign.getPrice()));
			line = line.replace("<intervall>", intervall);
			repl.add(line);
		}
		return repl;
	}
}
