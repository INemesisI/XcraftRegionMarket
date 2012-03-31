package me.INemesisI.XcraftRegionMarket.Handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.INemesisI.XcraftRegionMarket.Rent;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class RentHandler {
	private XcraftRegionMarket plugin;
	private ArrayList<Rent> rents = new ArrayList<Rent>();

	public ArrayList<Rent> getRents() {
		return rents;
	}

	public void setRents(ArrayList<Rent> rents) {
		this.rents = rents;
	}

	private SimpleDateFormat date = new SimpleDateFormat();

	public RentHandler(XcraftRegionMarket instance) {
		plugin = instance;
		date.applyPattern("yyyy.MM.dd HH:mm");
	}

	public void checkRents() {
		if (!rents.isEmpty()) for (Rent rent : rents) {
			checkRent(rent, getCurrentTime());
		}
	}

	private void checkRent(Rent rent, Date time) {
		if (!time.before(rent.getPaytime())) {
			Player player = plugin.getServer().getPlayer(rent.getOwner());
			if (plugin.getEconomy().getBalance(rent.getOwner()) >= rent.getPrice()) {
				plugin.getEconomy().depositPlayer(rent.getRenter(), rent.getPrice());
				plugin.getEconomy().withdrawPlayer(rent.getOwner(), rent.getPrice());
				if (player != null) {
					player.sendMessage(plugin.getCName() + "Dir wurde deine Miete von " + plugin.getEconomy().format(rent.getPrice()) + " abgezogen");
				}
				plugin.Debug("withdrew " + plugin.getEconomy().format(rent.getPrice()) + " from " + rent.getOwner() + " for renting region " + rent
						.getRegion());
				rent.setPaytime(getPaytime(time, rent.getIntervall()));
				plugin.Debug("next paytime: " + date.format(rent.getPaytime()));
				checkRent(rent, time);
			} else {
				if (player != null) {
					player.sendMessage(plugin.getCName() + "Du hattest nicht genügend Geld um deine Miete für " + rent.getRegion() + " zu bezahlen.");
					player.sendMessage(plugin.getCName() + "Die Rechte für diese Region wurden dir genommen!");
				}
				plugin.Debug(player + " had not enough money to pay his rented region " + rent.getRegion());
				plugin.regionHandler.removeAllPlayers(plugin.regionHandler.getRegion(rent));
			}
		}
	}

	private Date getPaytime(Date t, String i) {
		Calendar time = Calendar.getInstance();
		time.setTime(t);
		String[] split = i.split(" & ");
		int days = Integer.parseInt(split[0].split(" ")[0]);
		int hours = Integer.parseInt(split[1].split(" ")[0]);
		time.add(Calendar.DATE, days);
		time.add(Calendar.HOUR, hours);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		return time.getTime();
	}

	private Date getCurrentTime() {
		try {
			return date.parse(date.format(new Date()));
		} catch (ParseException e) {
		}
		return null;
	}

	public void add(Rent rent) {
		rents.add(rent);
		rent.setPaytime(getPaytime(getCurrentTime(), rent.getIntervall()));
	}

	public boolean remove(Rent rent) {
		return rents.remove(rent);
	}

	public boolean contains(Rent rent) {
		return rents.contains(rent);
	}

	public Rent getRent(Block block) {
		for (Rent rent : rents) {
			if (rent.getBlock().equals(block)) return rent;
		}
		return null;
	}
}
