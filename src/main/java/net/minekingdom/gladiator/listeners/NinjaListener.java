package net.minekingdom.gladiator.listeners;

import java.util.List;

import net.minekingdom.gladiator.Cooldown;
import net.minekingdom.gladiator.GladiatorExtension;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class NinjaListener implements Listener {

	private GladiatorExtension	plugin;

	public NinjaListener(GladiatorExtension plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void handleNinjaClick(PlayerInteractEvent event) {
		final Player player = event.getPlayer();

		if (player == null || !GladiatorExtension.isClass(player, "ninja")) {
			return;
		}
		
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
		
			switch (player.getItemInHand().getType()) {
				case STRING: {
					if (player.getItemInHand().getDurability() == 0) {
						for (Entity e : player.getNearbyEntities(30, 2, 30)) {
							if (e instanceof Creature && ((Creature) e).getTarget().equals(player)) {
								((Creature) e).setTarget(null);
							}
						}
						
						PotionEffect boost = new PotionEffect(PotionEffectType.SPEED, 5, 1);
						player.addPotionEffect(boost);
						
						PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, 5, 127);
						player.addPotionEffect(invisibility);
						
						PotionEffect blind = new PotionEffect(PotionEffectType.BLINDNESS, 5, 1);
						player.addPotionEffect(blind);
						
						player.getItemInHand().setDurability((short) 1);
						new Cooldown(player, Material.STRING).runTaskLater(plugin, 20 * 20L);
					}
					event.setCancelled(true);
				} break;
				case ENDER_PEARL: {
					if (player.getItemInHand().getDurability() == 0 && player.getEyeLocation().getPitch() >= Math.PI / 2.) {
						List<Block> foundBlocks = player.getLineOfSight(null, 20);
						
						if (!foundBlocks.isEmpty()) {
							player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
							Location target = foundBlocks.get(0).getLocation();
								target.setPitch(player.getLocation().getPitch());
								target.setYaw(player.getLocation().getYaw());
							
							player.teleport(target, TeleportCause.ENDER_PEARL);
							player.damage(4);
							
							player.getItemInHand().setDurability((short) 1);
							new Cooldown(player, Material.ENDER_PEARL).runTaskLater(plugin, 10 * 20L);
						}
					} else {
						player.setItemInHand(player.getItemInHand());
					}
					event.setCancelled(true);
				} break;
				case BLAZE_POWDER: {
					if (player.getItemInHand().getDurability() == 0) {
						Location eyeLocation = player.getEyeLocation();
						Vector velocity = eyeLocation.getDirection().multiply(2);
						player.getWorld().spawnEntity(eyeLocation.add(eyeLocation.getDirection()), EntityType.FIREBALL).setVelocity(velocity);
						
						player.getItemInHand().setDurability((short) 1);
						new Cooldown(player, Material.BLAZE_POWDER).runTaskLater(plugin, 20 * 20L);
					}
					event.setCancelled(true);
				} break;
				default: break;
			}
		}
	}

}
