package net.minekingdom.gladiator.listeners;

import net.minekingdom.gladiator.Cooldown;
import net.minekingdom.gladiator.GladiatorExtension;

import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TankListener implements Listener {
	
	private GladiatorExtension	plugin;

	public TankListener(GladiatorExtension plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void handleTankClick(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (player == null || !GladiatorExtension.isClass(player, "knight")) {
			return;
		}
		
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
		
			switch (player.getItemInHand().getType()) {
				case CLAY: {
					if (player.getItemInHand().getDurability() == 0) {
						for (Entity e : player.getNearbyEntities(30, 2, 30)) {
							if (e instanceof Creature) {
								((Creature) e).setTarget(player);
							}
						}
						
						player.getItemInHand().setDurability((short) 1);
						new Cooldown(player, Material.CLAY).runTaskLater(plugin, 20 * 20L);
					}
					event.setCancelled(true);
				} break;
				default: break;
			}
		}
	}

}
