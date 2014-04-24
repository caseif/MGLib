package net.amigocraft.mglib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.amigocraft.mglib.api.LobbySign;
import net.amigocraft.mglib.api.LobbyType;
import net.amigocraft.mglib.api.MGPlayer;
import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.event.player.PlayerJoinMinigameRoundEvent;
import net.amigocraft.mglib.event.player.PlayerLeaveMinigameRoundEvent;
import net.amigocraft.mglib.event.round.MinigameRoundEndEvent;
import net.amigocraft.mglib.event.round.MinigameRoundRollbackEvent;
import net.amigocraft.mglib.event.round.MinigameRoundStartEvent;
import net.amigocraft.mglib.event.round.MinigameRoundTickEvent;
import net.amigocraft.mglib.exception.ArenaNotExistsException;
import net.amigocraft.mglib.exception.PlayerNotPresentException;
import net.amigocraft.mglib.exception.PlayerOfflineException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

class MGListener implements Listener {

	static List<String> worlds = new ArrayList<String>();

	private static boolean PREVENT_BURN = true;
	private static boolean PREVENT_FADE = true;
	private static boolean PREVENT_GROW = true;
	private static boolean PREVENT_IGNITE = true;
	private static boolean PREVENT_LIQUIDFLOW = false;
	private static boolean PREVENT_PHYSICS = true;
	private static boolean PREVENT_PISTON = false;
	private static boolean PREVENT_SPREAD = true;

	public static void initialize(){
		PREVENT_BURN = Main.plugin.getConfig().getBoolean("protections.burn");
		PREVENT_FADE = Main.plugin.getConfig().getBoolean("protections.fade");
		PREVENT_GROW = Main.plugin.getConfig().getBoolean("protections.grow");
		PREVENT_IGNITE = Main.plugin.getConfig().getBoolean("protections.ignite");
		PREVENT_LIQUIDFLOW = Main.plugin.getConfig().getBoolean("protections.liquidFlow");
		PREVENT_PHYSICS = Main.plugin.getConfig().getBoolean("protections.physics");
		PREVENT_PISTON = Main.plugin.getConfig().getBoolean("protections.piston");
		PREVENT_SPREAD = Main.plugin.getConfig().getBoolean("protections.spread");
		for (Minigame mg : Minigame.getMinigameInstances())
			MGListener.addWorlds(mg.getPlugin());
	}

	public static void addWorlds(JavaPlugin plugin){
		File f = new File(plugin.getDataFolder(), "arenas.yml");
		if (f.exists()){
			YamlConfiguration y = new YamlConfiguration();
			try {
				y.load(f);
				for (String k : y.getKeys(false))
					if (!worlds.contains(y.getString(k + ".world")))
						worlds.add(y.getString(k + ".world"));
			}
			catch (Exception ex){
				ex.printStackTrace();
				Main.log.severe("An exception occurred while loading world list for plugin " + plugin.getName());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e){
		final String p = e.getPlayer().getName();
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){
			public void run(){
				for (Minigame mg : Minigame.getMinigameInstances())
					for (Round r : mg.getRoundList())
						if (r.getPlayers().containsKey(p)){
							try {
								r.removePlayer(p);
								YamlConfiguration y = new YamlConfiguration();
								File f = new File(Main.plugin.getDataFolder(), "offlineplayers.yml");
								if (!f.exists())
									f.createNewFile();
								y.load(f);
								Location el = mg.getConfigManager().getDefaultExitLocation();
								y.set(p + ".w", el.getWorld().getName());
								y.set(p + ".x", el.getX());
								y.set(p + ".y", el.getY());
								y.set(p + ".z", el.getZ());
								y.save(f);
							}
							catch (Exception ex){
								ex.printStackTrace();
								Main.log.severe("An exception occurred while saving data for " + p);
							}
						}
			}
		});
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e){
		final String p = e.getPlayer().getName();
		Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable(){
			public void run(){
				for (Minigame mg : Minigame.getMinigameInstances())
					for (Round r : mg.getRoundList())
						if (r.getPlayers().containsKey(p)){
							try {
								YamlConfiguration y = new YamlConfiguration();
								File f = new File(Main.plugin.getDataFolder(), "offlineplayers.yml");
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
								Main.log.severe("An exception occurred while loading data for " + p);
							}
						}
			}
		});
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent e){
		boolean found = false;
		for (Minigame mg : Minigame.getMinigameInstances()){
			for (Round r : mg.getRoundList()){
				MGPlayer p = r.getMGPlayer(e.getPlayer().getName());
				if (p != null){
					Location l = e.getPlayer().getLocation();
					if (!e.getPlayer().getWorld().getName().equals(r.getWorld())){
						try {
							p.removeFromRound(l);
						}
						catch (PlayerNotPresentException ex){}
						catch (PlayerOfflineException ex2){} // neither of these can happen
					}
					else {
						Location min = r.getMinBound();
						Location max = r.getMaxBound();
						if (min != null && max != null){
							if (l.getX() < min.getX() || 
									l.getY() < min.getY() ||
									l.getZ() < min.getZ() ||
									l.getX() > max.getX() ||
									l.getY() > max.getY() ||
									l.getZ() > max.getZ()){
								try {
									p.removeFromRound(l);
								}
								catch (PlayerNotPresentException ex){}
								catch (PlayerOfflineException ex){} // neither of these can happen
							}
						}
					}
					break;
				}
			}
			if (found)
				break;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent e){
		boolean found = false;
		for (Minigame mg : Minigame.getMinigameInstances()){
			for (Round r : mg.getRoundList()){
				if (r.getPlayers().containsKey(e.getWhoClicked().getName())){
					if (e.getInventory().getHolder() instanceof BlockState){
						mg.getRollbackManager().logInventoryChange(e.getInventory(),
								((BlockState)e.getInventory().getHolder()).getBlock(), r.getArena());
						found = true;
						break;
					}
				}
			}
			if (found)
				break;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e){
		for (Minigame mg : Minigame.getMinigameInstances())
			for (Round r : mg.getRoundList())
				if (r.getPlayers().containsKey(e.getPlayer().getName()))
					mg.getRollbackManager().logBlockChange(e.getBlock(),
							e.getBlockReplacedState().getType().toString(), r.getArena());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e){
		for (Minigame mg : Minigame.getMinigameInstances())
			for (Round r : mg.getRoundList())
				if (r.getPlayers().containsKey(e.getPlayer().getName()))
					mg.getRollbackManager().logBlockChange(e.getBlock(),
							e.getBlock().getType().toString(), r.getArena());
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
	public void onBlockPiston(BlockPistonExtendEvent e){
		if (PREVENT_PISTON && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPiston(BlockPistonRetractEvent e){
		if (PREVENT_PISTON && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockSpread(BlockSpreadEvent e){
		if (PREVENT_SPREAD && worlds.contains(e.getBlock().getWorld().getName()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e){
		if (e.getBlock().getState() instanceof Sign){ // just in case
			for (Minigame mg : Minigame.getMinigameInstances()){ // iterate registered minigames
				if (e.getLine(0).equalsIgnoreCase(mg.getConfigManager().getSignId())){ // it's a lobby sign-to-be
					if (e.getPlayer().hasPermission(mg.getPlugin().getName() + ".lobby.create")){
						if (!e.getLine(1).equalsIgnoreCase("players") ||
								MGUtil.isInteger(e.getLine(3))){ // make sure last line (sign index) is a number if it's a player sign
							try {
								int index = MGUtil.isInteger(e.getLine(3)) ? Integer.parseInt(e.getLine(3)) : 0;
								mg.getLobbyManager().add(e.getBlock().getLocation(), e.getLine(2), LobbyType.fromString(e.getLine(1)), index);
							}
							catch (ArenaNotExistsException ex){
								e.getPlayer().sendMessage(ChatColor.RED + "The specified arena does not exist!");
							}
							catch (IllegalArgumentException ex){
								if (ex.getMessage().contains("index"))
									e.getPlayer().sendMessage(ChatColor.RED + "The specified player sign index is not valid!");
								else if (ex.getMessage().contains("type"))
									e.getPlayer().sendMessage(ChatColor.RED + "The specified sign type is not valid!");
								else if (ex.getMessage().contains("Invalid string!"))
									e.getPlayer().sendMessage(ChatColor.RED + "Invalid sign type!");
								else
									ex.printStackTrace();
							}
						}
						else
							e.getPlayer().sendMessage(ChatColor.RED + "The specified player sign index is not valid!");
					}
					break;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if (e.getClickedBlock().getState() instanceof Sign){
				for (Minigame mg : Minigame.getMinigameInstances()){
					LobbySign ls = mg.getLobbyManager().getSign(e.getClickedBlock().getLocation());
					if (ls != null){
						e.setCancelled(true);
						if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getPlayer().isSneaking()){
							if (e.getPlayer().hasPermission(ls.getPlugin() + ".lobby.destroy")){
								e.setCancelled(false);
								ls.remove();
								return;
							}
						}
						MGPlayer p = mg.getMGPlayer(e.getPlayer().getName());
						if (p == null || p.getRound() == null){
							Round r = mg.getRound(ls.getArena());
							if (r == null){
								try {
									r = mg.createRound(ls.getArena());
								}
								catch (ArenaNotExistsException ex){
									e.getPlayer().sendMessage(ChatColor.RED + "Could not load arena " + ls.getArena() + "!");
								}
							}
							try {
								r.addPlayer(e.getPlayer().getName());
							}
							catch (PlayerOfflineException ex){} // this can never happen
						}
						else
							e.getPlayer().sendMessage(ChatColor.RED + "You are already in a round!");
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerJoinMinigameRound(PlayerJoinMinigameRoundEvent e){
		e.getRound().getMinigame().getLobbyManager().update(e.getRound().getArena());
	}

	@EventHandler
	public void onPlayerLeaveMinigameRound(PlayerLeaveMinigameRoundEvent e){
		e.getRound().getMinigame().getLobbyManager().update(e.getRound().getArena());
	}

	@EventHandler
	public void onMinigameRoundStart(MinigameRoundStartEvent e){
		e.getRound().getMinigame().getLobbyManager().update(e.getRound().getArena());
	}

	@EventHandler
	public void onMinigameRoundEnd(MinigameRoundEndEvent e){
		e.getRound().getMinigame().getLobbyManager().update(e.getRound().getArena());
	}

	@EventHandler
	public void onMinigameRoundTick(MinigameRoundTickEvent e){
		e.getRound().getMinigame().getLobbyManager().update(e.getRound().getArena());
	}

	@EventHandler
	public void onMinigameRoundRollback(MinigameRoundRollbackEvent e){
		e.getRound().getMinigame().getLobbyManager().update(e.getRound().getArena());
	}
	
}
