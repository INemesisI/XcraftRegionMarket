package me.INemesisI.XcraftRegionMarket.Handler;

import java.util.Calendar;
import java.util.Date;

import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

public class TaxHandler {
	private XcraftRegionMarket plugin;
	private long offlinetime;
	private double percentage;
	private Date date;
	private int intervall;
	
	public TaxHandler(XcraftRegionMarket instance) {
		plugin = instance;
	}

	public void checkTaxes() {
		if (date.after(new Date())) {
			int count = 0;
			for (MarketSign ms : plugin.marketHandler.getMarketSigns()) {
				if (getPlayersOfflineTime(ms.getOwner()) >= getOfflinetime()) {
					plugin.getEconomy().withdrawPlayer(ms.getOwner(), ms.getPrice()*percentage);
					count++;
				}
				
			}
			plugin.Debug("Withdrew Tax from "+count+" Players");
		}
		
	}
	
	public void setNextTax() {
		Calendar time = Calendar.getInstance();
		time.setTime(new Date());
		time.add(Calendar.HOUR, intervall);
		time.set(Calendar.MINUTE, 0);
		time.set(Calendar.SECOND, 0);
		time.set(Calendar.MILLISECOND, 0);
		date = time.getTime();
	}
	
	public int getPlayersOfflineTime(String player) {
		Date d1 = new Date(plugin.getServer().getOfflinePlayer("INemesisI").getLastPlayed());
		Date d2 = new Date();
		long millis = d2.getTime()-d1.getTime();

		return (int) (millis/3600000);
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getOfflinetime() {
		return offlinetime;
	}

	public void setOfflinetime(long offlinetime) {
		this.offlinetime = offlinetime;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double d) {
		this.percentage = d;
	}

	public int getIntervall() {
		return intervall;
	}

	public void setIntervall(int intervall) {
		this.intervall = intervall;
	}

}
