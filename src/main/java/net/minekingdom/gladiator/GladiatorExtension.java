package net.minekingdom.gladiator;

import net.minekingdom.gladiator.listeners.BarbarianListener;
import net.minekingdom.gladiator.listeners.GladiatorListener;
import net.minekingdom.gladiator.listeners.NinjaListener;
import net.minekingdom.gladiator.listeners.TankListener;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GladiatorExtension extends JavaPlugin {
	
	public final static String PERMISSION_PREFIX = "minekingdom.mobarena.";
	
	private GladiatorListener listener;

	public void onEnable() {
		getServer().getPluginManager().registerEvents(listener = new GladiatorListener(this), this);
		getServer().getPluginManager().registerEvents(new NinjaListener(this), this);
		getServer().getPluginManager().registerEvents(new TankListener(this), this);
		getServer().getPluginManager().registerEvents(new BarbarianListener(this), this);
		
		getLogger().info(getDescription().getName() + " version " + getDescription().getVersion() + " is enabled!");
	}
	
	public void onDisable() {
		listener.onDisable();
	}
	
	public static boolean isGladiator(Player target) {
		return target.hasPermission(PERMISSION_PREFIX + "gladiator");
	}
	
	public static boolean isClass(Player target, String classname) {
		return target.hasPermission(PERMISSION_PREFIX + "class." + classname);
	}
}