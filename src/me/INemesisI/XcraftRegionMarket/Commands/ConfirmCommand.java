package me.INemesisI.XcraftRegionMarket.Commands;

import java.util.Date;
import java.util.List;

import me.INemesisI.XcraftRegionMarket.MarketSign;
import me.INemesisI.XcraftRegionMarket.MarketSign.Type;
import me.INemesisI.XcraftRegionMarket.RentSign;
import me.INemesisI.XcraftRegionMarket.XcraftRegionMarket;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConfirmCommand extends CommandHelper {

	public ConfirmCommand(XcraftRegionMarket instance) {
		super(instance);
	}

	@Override
	public void execute(CommandSender sender, String Command, List<String> args) {
		this.init(sender);

		MarketSign ms = this.getClicked().get(sender.getName());
		if (ms == null) {
			this.error("Du musst erst ein MarketSign auswählen!");
			return;
		}
		if ((ms.getType() != Type.SELLING) && (ms.getType() != Type.RENTING)) {
			return;
		}

		if (ms.getOwner().equals(sender.getName())) {
			return;
		}

		if (!(economy.getBalance(ms.getOwner()) >= ms.getPrice())) {
			this.error("Du hast nicht genug Geld!");
			return;
		}

		if (!plugin.regionHandler.canBuy(player, ms.getType(), ms.getRegion())) {
			this.error("Du hast dein Limit an Regionen erreicht. Du kannst hier nicht noch mehr Regionen besitzen!");
			return;
		}

		if ((ms.getType() == Type.SELLING) && player.hasPermission("XcraftRegionMarket.Buy")) {
			// inform the players
			Player seller = plugin.getServer().getPlayer(ms.getOwner());
			if (seller != null) {
				seller.sendMessage(plugin.getName() + player.getName() + " hat dein Grundstück " + ms.getRegion()
						+ " für " + economy.format(ms.getPrice()) + " gekauft!");
			}
			this.reply("Du hast das Grundstück " + ms.getRegion() + " von " + ms.getOwner() + " für " + ms.getPrice()
					+ " gekauft!");
			// set sign text
			ms.setType(Type.SOLD);
			ms.setOwner(player.getName());
			plugin.marketHandler.update(ms);
			// set group
			plugin.groupHandler.setPermGroup(player, ms.getType(), ms.getRegion().getParent().getId());
			// save region
			plugin.regionHandler.saveRegion(ms.getBlock().getWorld());
			// set money
			economy.depositPlayer(ms.getOwner(), ms.getPrice());
			economy.withdrawPlayer(player.getName(), ms.getPrice());
			// set regionowner
			plugin.regionHandler.setPlayer(ms.getRegion(), player.getName());
		}
		if ((ms.getType() == Type.RENTING) && player.hasPermission("XcraftRegionMarket.Rent")) {
			// set rentsign
			RentSign sign = (RentSign) ms;
			sign.setRenter(sign.getOwner());
			sign.setOwner(player.getName());
			sign.setType(Type.RENTED);
			sign.setNextPaytime(new Date());
			// inform the players
			Player seller = plugin.getServer().getPlayer(sign.getRenter());
			if (seller != null) {
				seller.sendMessage(plugin.getName() + ms.getOwner() + " hat dein Grundstück " + ms.getRegion()
						+ " für " + economy.format(ms.getPrice()) + " gemietet!");
			}
			this.reply("Du hast das Grundstück " + ms.getRegion() + " von " + sign.getRenter() + " für "
					+ economy.format(ms.getPrice()) + " pro " + sign.getIntervall() + " gemietet!");
			// update sign
			plugin.marketHandler.update(ms);
			// set group
			plugin.groupHandler.setPermGroup(player, ms.getType(), ms.getRegion().getParent().getId());
			// save region
			plugin.regionHandler.saveRegion(ms.getBlock().getWorld());
			// set regionowner
			plugin.regionHandler.setPlayer(ms.getRegion(), player.getName());
		}

	}

}
