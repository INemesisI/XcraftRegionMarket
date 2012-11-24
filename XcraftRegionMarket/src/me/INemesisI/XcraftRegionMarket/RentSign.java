package me.INemesisI.XcraftRegionMarket;

import java.util.Calendar;
import java.util.Date;

import org.bukkit.block.Block;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RentSign extends MarketSign {
	private Date paytime;
	private String renter;
	private String intervall;

	public RentSign(Block block, ProtectedRegion region, Type type, String owner, double price, String renter, String intervall, Date paytime) {
		super(block, region, type, owner, price);
		setRenter(renter);
		setIntervall(intervall);
		setPaytime(paytime);
	}

	public RentSign(Block block, ProtectedRegion region, Type type, String owner, double price, Globalprice gp, String renter, String intervall,
			Date paytime) {
		super(block, region, type, owner, price, gp);
		setRenter(renter);
		setIntervall(intervall);
		setPaytime(paytime);
	}

	public RentSign(Block block, ProtectedRegion region, Type type, double price, Globalprice gp, String renter, String intervall, Date date) {
		super(block, region, type, renter, price, gp);
		setRenter(renter);
		setIntervall(intervall);
		setNextPaytime(date);
	}

	public boolean rentTo(String player, Date t) {
		if (type == Type.RENTED) return false;
		setRenter(getOwner());
		setOwner(player);
		setType(Type.RENTED);
		setNextPaytime(t);
		return true;
	}

	public boolean unrent() {
		if (type == Type.RENTING) return false;
		setOwner(getRenter());
		setType(Type.RENTING);
		setPaytime(null);
		return true;
	}

	public Date setNextPaytime(Date t) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(t);
		String[] split = intervall.split(" & ");
		int days = Integer.parseInt(split[0].split(" ")[0]);
		int hours = Integer.parseInt(split[1].split(" ")[0]);
		calendar.add(Calendar.DATE, days);
		calendar.add(Calendar.HOUR, hours);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	public Date getPaytime() {
		return paytime;
	}

	public void setPaytime(Date paytime) {
		this.paytime = paytime;
	}

	public String getRenter() {
		return renter;
	}

	public void setRenter(String renter) {
		this.renter = renter;
	}

	public String getIntervall() {
		return intervall;
	}

	public void setIntervall(String intervall) {
		intervall = intervall;
	}
}
