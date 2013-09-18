package net.minekingdom.gladiator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Cooldown extends BukkitRunnable {
	
	private Material mat;
	private Player player;

	public Cooldown(Player player, Material mat) {
		this.player = player;
		this.mat = mat;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		if (player.isOnline()) {
			for (ItemStack stack : player.getInventory().getContents()) {
				if (stack != null && stack.getType().equals(mat) && stack.getDurability() != 0) {
					player.getInventory().remove(stack);
					player.updateInventory();
					stack.setDurability((short) 0);
					player.getInventory().addItem(stack);
					player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 3, 0);
				}
			}
		}
	}

}
