package net.minekingdom.gladiator.listeners;

import net.minekingdom.gladiator.Cooldown;
import net.minekingdom.gladiator.GladiatorExtension;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BarbarianListener implements Listener {

	private GladiatorExtension	plugin;

	public BarbarianListener(GladiatorExtension plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void handleBarbarianClick(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (player == null || !GladiatorExtension.isClass(player, "barbarian")) {
			return;
		}
		
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			switch (player.getItemInHand().getType()) {
				case MAGMA_CREAM: {
					if (player.getItemInHand().getDurability() == 0) {
						for (Entity e : player.getNearbyEntities(5, 5, 5)) {
							if (e instanceof Player) {
								Player target = (Player) e;
								if (GladiatorExtension.isGladiator(target)) {
									PotionEffect warcry = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5, 1);
									target.addPotionEffect(warcry);
								}
							}
						}
						
						player.getWorld().playSound(player.getLocation(), Sound.WOLF_BARK, 3, 0);
						
						player.getItemInHand().setDurability((short) 1);
						new Cooldown(player, Material.MAGMA_CREAM).runTaskLater(plugin, 30 * 20L);
					}
					event.setCancelled(true);
				} break;
				default: break;
			}
		}
	}
}
