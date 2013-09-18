package net.minekingdom.gladiator.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minekingdom.gladiator.GladiatorExtension;
import net.minekingdom.gladiator.MerchantItem;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GladiatorListener implements Listener {
	
	@SuppressWarnings("unused")
	private GladiatorExtension	plugin;
	
	private HashMap<Block, ItemStack> cauldrons = new HashMap<Block, ItemStack>();
	
	private Inventory inv = null;
	
	private final List<Player> tradingPlayers = new ArrayList<Player>();
	private final List<Player> traderNearbyPlayers = new ArrayList<Player>();

	public GladiatorListener(GladiatorExtension plugin) {
		this.plugin = plugin;
		
		inv = Bukkit.getServer().createInventory(null, 9, "Marchand");
		
		inv.addItem(new ItemStack(Material.ENCHANTMENT_TABLE));
		inv.addItem(new ItemStack(Material.BOOKSHELF));
		inv.addItem(new ItemStack(Material.ANVIL));
		inv.addItem(new ItemStack(Material.CAULDRON_ITEM));
		inv.addItem(new ItemStack(Material.GRILLED_PORK));
		inv.addItem(new ItemStack(Material.GLASS_BOTTLE));
		inv.addItem(new ItemStack(Material.GOLDEN_APPLE));
	}
	
	public void onDisable() {
		for (Block block : cauldrons.keySet()) {
			block.setData((byte) 0);
		}
		cauldrons.clear();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {	
		final Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if (player == null || !GladiatorExtension.isGladiator(player)) {
			return;
		}
		
		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && block.getType().equals(Material.ENCHANTMENT_TABLE) && block.getWorld().getName().equalsIgnoreCase("events") && player.hasPermission("gladiator")) {

			ItemStack item = player.getItemInHand();
			
			if (item == null) {
				event.setCancelled(true);
				return;
			}
			
			if (item.getEnchantments().size() > 0) {
				player.sendMessage(ChatColor.GREEN + "Votre objet a été désenchanté.");
				
				for (Enchantment ench : item.getEnchantments().keySet()) {
					item.removeEnchantment(ench);
				}
				
				player.setItemInHand(item);
				player.updateInventory();
			}
			
			event.setCancelled(true);
		} else if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			
			if (block.getType().equals(Material.ANVIL)) {
				event.setCancelled(true);
				
				ItemStack item = player.getItemInHand();
				
				if ( item == null ) {
					return;
				}
				
				if ( item.getDurability() > 0 ) {
					if ( player.getLevel() == 0 ) {
						player.sendMessage(ChatColor.GREEN + "Vous n'avez pas assez d'experience pour réparer cet objet.");
					} else {
						player.sendMessage(ChatColor.GREEN + "Votre objet a été réparé.");
						
						player.setLevel(player.getLevel() - 1);
						int xp = (int) 3.5*player.getLevel()*(player.getLevel() + 1);
						
						player.setTotalExperience(xp);
						
						player.setExp(0);								

						item.setDurability((short) 0);
						player.setItemInHand(item);
					}
				}
				
			} else if (block.getType().equals(Material.CAULDRON)) {
				
				if (block.getData() == 0 && player.getItemInHand().getType().equals(Material.POTION) && player.hasPermission("alchemist")) {
					ItemStack item = player.getItemInHand().clone();
					item.setAmount(1);
					ItemStack add = new ItemStack(Material.GLASS_BOTTLE, 1);
					
					player.getInventory().removeItem(item);
					player.getInventory().addItem(add);
					
					cauldrons.put(block, item);
					block.setData((byte) 3);
					
					player.sendMessage(ChatColor.GREEN + "Vous versez la potion dans le chaudron.");
				} else if (player.getItemInHand().getType().equals(Material.GLASS_BOTTLE)) {
					if (!cauldrons.containsKey(block)) {
						return;
					}
					
					ItemStack remove = player.getItemInHand().clone();
					remove.setAmount(1);
					ItemStack add = cauldrons.get(block);
					
					player.getInventory().removeItem(remove);
					player.getInventory().addItem(add);
					
					if (block.getData() == 1) { // 1/3
						cauldrons.remove(block);
					}
					block.setData((byte) (block.getData() - 1));
					
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		
		if (event.isCancelled()) {
			return;
		}
		
		final Player player = event.getPlayer();

		if (player == null || !GladiatorExtension.isGladiator(player)) {
			return;
		}
		
		if (!traderNearbyPlayers.contains(player)) {
			List<Entity> entities = player.getNearbyEntities(5, 5, 5);
			for ( Entity entity : entities )
			{
				if ( entity instanceof Villager )
				{
					traderNearbyPlayers.add(player);
					player.sendMessage(ChatColor.DARK_AQUA + "Marchand: " + ChatColor.AQUA + "Salutations ! Je vends des articles de touts genres pour vous aider dans votre combat ! Quelque-chose vous intéresserait ?");
					break;
				}
			}
		} else {
			List<Entity> entities = player.getNearbyEntities(5, 5, 5);
			boolean isNear = false;
			
			for ( Entity entity : entities ) {
				if ( entity instanceof Villager ) {
					isNear = true;
					break;
				}
			}
			
			if (!isNear) {
				traderNearbyPlayers.remove(player);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		final Player player = event.getPlayer();
		final Entity entity = event.getRightClicked();

		if (player == null || entity == null || !GladiatorExtension.isGladiator(player)) {
			return;
		}
		
		if (entity instanceof Villager) {
			player.openInventory(inv);
			tradingPlayers.add(player);
			event.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if ( !(event.getWhoClicked() instanceof Player) )
			return;
		
		Player player = (Player) event.getWhoClicked();

		if ( tradingPlayers.contains(player) ) {
			try {
				MerchantItem merchantItem = MerchantItem.valueOf(event.getCurrentItem().getData().getItemType().toString());
			
				if ( player.getLevel() - merchantItem.getPrice() <= 0 )
				{
					player.sendMessage(ChatColor.RED + "Vous n'avez pas assez d'experience pour acheter cet objet.");
				} else {
					
					player.setLevel(player.getLevel() - merchantItem.getPrice());
					int xp = (int) 3.5*player.getLevel()*(player.getLevel() + 1);
					
					player.setTotalExperience(xp);
					
					player.setExp(0); //update xp bar
					
					player.getInventory().addItem(new ItemStack(event.getCurrentItem().getData().getItemType(), 1));
					player.updateInventory();
				}
			} catch (Exception e) {
			}

			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if ( !(event.getPlayer() instanceof Player) ) {
			return;
		}

		Player player = (Player) event.getPlayer();
		
		if ( tradingPlayers.contains(event.getPlayer()) ) {
			tradingPlayers.remove(event.getPlayer());
			player.sendMessage(ChatColor.GREEN + "Transaction terminée.");
		}
	}

}
