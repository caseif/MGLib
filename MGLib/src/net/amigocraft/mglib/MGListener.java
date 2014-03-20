package net.amigocraft.mglib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.amigocraft.mglib.api.MGPlayer;
import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.api.Round;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

class MGListener implements Listener {

	private static List<String> worlds = new ArrayList<String>();

	private static boolean PREVENT_BREAK = true;
	private static boolean PREVENT_BURN = true;
	private static boolean PREVENT_FADE = true;
	private static boolean PREVENT_GROW = true;
	private static boolean PREVENT_IGNITE = true;
	private static boolean PREVENT_LIQUIDFLOW = false;
	private static boolean PREVENT_PHYSICS = true;
	private static boolean PREVENT_PISTON = false;
	private static boolean PREVENT_PLACE = true;
	private static boolean PREVENT_SPREAD = true;

	public static void initialize(){
		PREVENT_BREAK = MGLib.plugin.getConfig().getBoolean("protections.break");
		PREVENT_BURN = MGLib.plugin.getConfig().getBoolean("protections.burn");
		PREVENT_FADE = MGLib.plugin.getConfig().getBoolean("protections.fade");
		PREVENT_GROW = MGLib.plugin.getConfig().getBoolean("protections.grow");
		PREVENT_IGNITE = MGLib.plugin.getConfig().getBoolean("protections.ignite");
		PREVENT_LIQUIDFLOW = MGLib.plugin.getConfig().getBoolean("protections.liquidFlow");
		PREVENT_PHYSICS = MGLib.plugin.getConfig().getBoolean("protections.physics");
		PREVENT_PISTON = MGLib.plugin.getConfig().getBoolean("protections.piston");
		PREVENT_PLACE = MGLib.plugin.getConfig().getBoolean("protections.place");
		PREVENT_SPREAD = MGLib.plugin.getConfig().getBoolean("protections.spread");
		for (Minigame mg : Minigame.getMinigameInstances())
			MGListener.addWorlds(mg.getPlugin());
	}

	public static void addWorlds(JavaPlugin plugin){
		File f = new File(plugin.getDataFolder(), "arenas.yml");
		YamlConfiguration y = new YamlConfiguration();
		try {
			y.load(f);
			for (String k : y.getKeys(false))
				if (!worlds.contains(y.getString(k + ".world")))
					worlds.add(y.getString(k + ".world"));
		}
		catch (Exception ex){
			ex.printStackTrace();
			MGLib.log.severe("An exception occurred while loading world list for plugin " + plugin.getName());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e){
		final String p = e.getPlayer().getName();
		Bukkit.getScheduler().runTaskAsynchronously(MGLib.plugin, new Runnable(){
			public void run(){
				for (Minigame mg : Minigame.getMinigameInstances())
					for (Round r : mg.getRounds())
						if (r.getPlayers().containsKey(p)){
							try {
								r.removePlayer(p);
								YamlConfiguration y = new YamlConfiguration();
								File f = new File(MGLib.plugin.getDataFolder(), "offlineplayers.yml");
								if (!f.exists())
									f.createNewFile();
								y.load(f);
								y.set(p + ".w", mg.getExitLocation().getWorld().getName());
								y.set(p + ".x", mg.getExitLocation().getX());
								y.set(p + ".y", mg.getExitLocation().getY());
								y.set(p + ".z", mg.getExitLocation().getZ());
								y.save(f);
							}
							catch (Exception ex){
								ex.printStackTrace();
								MGLib.log.severe("An exception occurred while saving data for " + p);
							}
						}
			}
		});
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e){
		final String p = e.getPlayer().getName();
		Bukkit.getScheduler().runTaskAsynchronously(MGLib.plugin, new Runnable(){
			public void run(){
				for (Minigame mg : Minigame.getMinigameInstances())
					for (Round r : mg.getRounds())
						if (r.getPlayers().containsKey(p)){
							try {
								YamlConfiguration y = new YamlConfiguration();
								File f = new File(MGLib.plugin.getDataFolder(), "offlineplayers.yml");
								if (!f.exists())
									f.createNewFile();
								y.load(f);
								if (y.isSet("players")){
									if (y.isSet(p))
										MGPlayer.resetPlayer(p, new Location(
												Bukkit.getWorld(y.getString(p + ".w")),
												y.getDouble(p + ".x"),
												y.getDouble(p + ".y"),
												y.getDouble(p + ".z")));
								}
							}
							catch (Exception ex){
								ex.printStackTrace();
								MGLib.log.severe("An exception occurred while loading data for " + p);
							}
						}
			}
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e){
		if (PREVENT_PLACE && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
		else
			for (Minigame mg : Minigame.getMinigameInstances()){
				MGPlayer p = mg.getMGPlayer(e.getPlayer().getName());
				RollbackManager.logPhysical(e.getBlock(), e.getBlockReplacedState().getType().toString(), p.getArena());
			}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e){
		if (PREVENT_BREAK && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
		else
			for (Minigame mg : Minigame.getMinigameInstances()){
				MGPlayer p = mg.getMGPlayer(e.getPlayer().getName());
				RollbackManager.logPhysical(e.getBlock(), e.getBlock().getType().toString(), p.getArena());
			}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent e){
		if (PREVENT_BURN && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockFade(BlockFadeEvent e){
		if (PREVENT_FADE && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockGrow(BlockGrowEvent e){
		if (PREVENT_GROW && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockIgnite(BlockIgniteEvent e){
		if (PREVENT_IGNITE && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockFlow(BlockFromToEvent e){
		if (PREVENT_LIQUIDFLOW && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent e){
		if (PREVENT_PHYSICS && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPiston(BlockPistonEvent e){
		if (PREVENT_PISTON && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockSpread(BlockSpreadEvent e){
		if (PREVENT_SPREAD && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
	}

}
