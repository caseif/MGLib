/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Maxim Roncacé
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.amigocraft.mglib;

import static net.amigocraft.mglib.Main.locale;

import net.amigocraft.mglib.api.LobbySign;
import net.amigocraft.mglib.api.LobbyType;
import net.amigocraft.mglib.api.Location3D;
import net.amigocraft.mglib.api.MGPlayer;
import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.event.player.MGPlayerDeathEvent;
import net.amigocraft.mglib.event.round.LobbyClickEvent;
import net.amigocraft.mglib.exception.InvalidLocationException;
import net.amigocraft.mglib.exception.NoSuchArenaException;
import net.amigocraft.mglib.exception.NoSuchPlayerException;
import net.amigocraft.mglib.exception.PlayerOfflineException;
import net.amigocraft.mglib.exception.PlayerPresentException;
import net.amigocraft.mglib.exception.RoundFullException;
import net.amigocraft.mglib.misc.JoinResult;
import net.amigocraft.mglib.util.NmsUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

class MGListener implements Listener {

	public static HashMap<String, List<String>> worlds = new HashMap<String, List<String>>();

	static void initialize() {
		for (Minigame mg : Minigame.getMinigameInstances()) {
			MGListener.addWorlds(mg.getPlugin().getName());
		}
	}

	static void addWorlds(String plugin) {
		File f = new File(Bukkit.getPluginManager().getPlugin(plugin).getDataFolder(), "arenas.yml");
		if (f.exists()) {
			YamlConfiguration y = new YamlConfiguration();
			try {
				List<String> worldList = new ArrayList<String>();
				y.load(f);
				for (String k : y.getKeys(false)) {
					if (y.isSet(k + ".world")) {
						worldList.add(y.getString(k + ".world"));
					}
				}
				worlds.put(plugin, worldList);
			}
			catch (Exception ex) {
				ex.printStackTrace();
				Main.log.severe(Main.locale.getMessage("plugin.alert.world-list.load", plugin));
			}
		}
	}

	/**
	 * Retrieves worlds registered with MGLib's event listener.
	 *
	 * @return worlds registered with MGLib's event listener
	 * @since 0.1.0
	 */
	public static List<String> getWorlds() {
		List<String> worlds = new ArrayList<String>();
		for (List<String> l : MGListener.worlds.values()) {
			for (String w : l) {
				if (!worlds.contains(w)) {
					worlds.add(w);
				}
			}
		}
		return worlds;
	}

	/**
	 * Retrieves worlds registered with MGLib's event listener for the given
	 * plugin.
	 *
	 * @param plugin the plugin to retrieve worlds for
	 * @return worlds registered with MGLib's event listener for the given
	 * plugin
	 * @since 0.2.0
	 */
	public static List<String> getWorlds(String plugin) {
		if (MGListener.worlds.containsKey(plugin)) {
			return MGListener.worlds.get(plugin);
		}
		else {
			List<String> l = new ArrayList<String>();
			MGListener.worlds.put(plugin, l);
			return l;
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	// so that we can prepare everything for the hooking plugins
	public void onPlayerQuit(PlayerQuitEvent e) {
		final String p = e.getPlayer().getName();
		for (Minigame mg : Minigame.getMinigameInstances()) {
			for (Round r : mg.getRoundList()) {
				MGPlayer mp = r.getMGPlayer(p);
				if (mp != null) {
					try {
						mp.removeFromRound();
						// this bit is so it won't break when I'm testing, but offline servers will still get screwed up
						List<String> testAccounts = Arrays.asList("testing123", "testing456", "testing789");
						if (!testAccounts.contains(e.getPlayer().getName().toLowerCase())) {
							String pUuid = UUIDFetcher.getUUIDOf(p).toString();
							UUIDFetcher.removeUUID(p);
							YamlConfiguration y = new YamlConfiguration();
							File f = new File(Main.plugin.getDataFolder(), "offlineplayers.yml");
							if (!f.exists()) {
								f.createNewFile();
							}
							y.load(f);
							Location el = mg.getConfigManager().getDefaultExitLocation();
							y.set(pUuid + ".w", el.getWorld().getName());
							y.set(pUuid + ".x", el.getX());
							y.set(pUuid + ".y", el.getY());
							y.set(pUuid + ".z", el.getZ());
							y.save(f);
						}
					}
					catch (Exception ex) {
						ex.printStackTrace();
						Main.log.severe(locale.getMessage("plugin.alert.player-data.save", p));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent e) {
		Player pl = null;
		Player p2 = null; // don't ask why it's named like this
		if (e.getEntity() instanceof Player) {
			p2 = (Player)e.getEntity();
		}
		if (e instanceof EntityDamageByEntityEvent) {
			Entity damager = ((EntityDamageByEntityEvent)e).getDamager();
			if (damager instanceof Player) { // damager is a player
				pl = (Player)damager;
			}
			else if (damager instanceof Projectile) { // damager is an arrow or something
				if (((Projectile)damager).getShooter() instanceof Player) {
					// a player shot the projectile (e.g. an arrow from a bow)
					pl = (Player)((Projectile)damager).getShooter();
				}
			}

			//TODO: probably rewrite this bit at some point
			if (pl != null || p2 != null) {
				for (Minigame mg : Minigame.getMinigameInstances()) {
					if (p2 != null) {
						MGPlayer p = mg.getMGPlayer(p2.getName());
						if (p != null) {
							if (p.isSpectating() || !p.getRound().isDamageAllowed()) {
								e.setCancelled(true); // we don't want any spooky ghosts being harassed by the living
								return;
							}
						}
					}
					if (pl != null) {
						MGPlayer p = mg.getMGPlayer(pl.getName());
						if (p != null && (p.isSpectating() || !p.getRound().isPvPAllowed())) {
							e.setCancelled(true); // we don't want any spooky ghosts harassing the living
							return;
						}
						else if (p != null &&
								!mg.getConfigManager().isItemFrameDamageAllowed() &&
								e.getEntity() instanceof ItemFrame) {
							e.setCancelled(true);
							return;
						}
					}
					if (pl != null && p2 != null) {
						MGPlayer m1 = mg.getMGPlayer(pl.getName());
						MGPlayer m2 = mg.getMGPlayer(p2.getName());
						if (m1 != null &&
								m2 != null &&
								!mg.getConfigManager().isTeamDamageAllowed() &&
								m1.getTeam() != null &&
								m2.getTeam() != null &&
								m1.getTeam().equalsIgnoreCase(m2.getTeam())) {
							e.setCancelled(true);
							return;
						}
					}
				}
			}
		}
		for (Minigame mg : Minigame.getMinigameInstances()) {
			if (p2 != null) {
				MGPlayer p = mg.getMGPlayer(p2.getName());
				if (p != null && p.isSpectating()) {
					e.setCancelled(true);
					return;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		for (Minigame mg : Minigame.getMinigameInstances()) {
			if (mg.getConfigManager().isOverrideDeathEvent() && mg.isPlayer(e.getEntity().getName())) {
				e.setDeathMessage(null);
				e.setKeepLevel(true);
				e.getDrops().clear();
				try {
					NmsUtil.sendRespawnPacket(e.getEntity());
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
				EntityDamageEvent ed = e.getEntity().getLastDamageCause();
				MGUtil.callEvent(new MGPlayerDeathEvent(mg.getMGPlayer(e.getEntity().getName()), ed.getCause(),
						ed instanceof EntityDamageByEntityEvent ?
								((EntityDamageByEntityEvent)ed).getDamager() instanceof Projectile ?
										(Entity)((Projectile)((EntityDamageByEntityEvent)ed).getDamager())
												.getShooter() :
										((EntityDamageByEntityEvent)ed).getDamager()
								: null));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		for (Minigame mg : Minigame.getMinigameInstances()) {
			if (mg.getConfigManager().isOverrideDeathEvent() && mg.isPlayer(e.getPlayer().getName())) {
				e.setRespawnLocation(e.getPlayer().getLocation());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	// so that we can prepare everything for the hooking plugins
	public void onPlayerJoin(PlayerJoinEvent e) {
		final String p = e.getPlayer().getName();
		try {
			UUIDFetcher.addUUID(p, UUIDFetcher.getUUIDOf(p));
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Main.log.severe(Main.locale.getMessage("plugin.alert.uuid-fail.spec", p));
		}
		try {
			YamlConfiguration y = new YamlConfiguration();
			File f = new File(Main.plugin.getDataFolder(), "offlineplayers.yml");
			if (!f.exists()) {
				f.createNewFile();
			}
			y.load(f);
			UUID pUuid = UUIDFetcher.getUUIDOf(p);
			if (pUuid == null) {
				// this bit is so it won't break when I'm testing, but offline servers will still get screwed up
				List<String> testAccounts = Arrays.asList("testing123", "testing456", "testing789");
				if (testAccounts.contains(e.getPlayer().getName().toLowerCase())) {
					pUuid = e.getPlayer().getUniqueId();
				}
			}
			if (y.isSet(pUuid.toString())) {
				final String ww = y.getString(pUuid + ".w");
				final double xx = y.getDouble(pUuid + ".x");
				final double yy = y.getDouble(pUuid + ".y");
				final double zz = y.getDouble(pUuid + ".z");
				MGPlayer mp = new MGPlayer("MGLib", p, "null");
				mp.reset(new Location(Bukkit.getWorld(ww), xx, yy, zz));
				y.set(pUuid.toString(), null);
				y.save(f);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Main.log.severe(Main.locale.getMessage("plugin.alert.player-data.load", p));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		boolean found = false;
		for (Minigame mg : Minigame.getMinigameInstances()) {
			for (Round r : mg.getRoundList()) {
				MGPlayer p = r.getMGPlayer(e.getPlayer().getName());
				if (p != null) {
					Location l = e.getTo();
					if (!l.getWorld().getName().equals(r.getWorld())) {
						found = true;
						try {
							p.removeFromRound(l);
						}
						catch (NoSuchPlayerException ex) { // this can never happen
							ex.printStackTrace();
						}
						catch (PlayerOfflineException ex) { // this can definitely never happen
							ex.printStackTrace();
						}
					}
					else {
						Location min = r.getMinBound();
						Location max = r.getMaxBound();
						if (min != null && max != null) {
							if (l.getX() < min.getX() ||
									l.getY() < min.getY() ||
									l.getZ() < min.getZ() ||
									l.getX() > max.getX() ||
									l.getY() > max.getY() ||
									l.getZ() > max.getZ()) {
								found = true;
								try {
									p.removeFromRound(l);
								}
								catch (NoSuchPlayerException ex) { // this can never happen
									ex.printStackTrace();
								}
								catch (PlayerOfflineException ex) { // this can definitely never happen
									ex.printStackTrace();
								}
							}
						}
					}
					break;
				}
			}
			if (found) {
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e) {
		for (Minigame mg : Minigame.getMinigameInstances()) {
			MGPlayer mp = mg.getMGPlayer(e.getWhoClicked().getName());
			if (mp != null) {
				if (mp.isSpectating()) {
					e.setCancelled(true);
					return;
				}
				if (e.getInventory().getHolder() instanceof BlockState) {
					mg.getRollbackManager().logInventoryChange(e.getInventory(),
							((BlockState)e.getInventory().getHolder()).getBlock(), mp.getArena());
					return;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent e) {
		for (Minigame mg : Minigame.getMinigameInstances()) {
			for (Round r : mg.getRoundList()) {
				if (r.isRollbackEnabled()) {
					if (r.getPlayers().containsKey(e.getPlayer().getName())) {
						if (!mg.getConfigManager().isBlockPlaceAllowed()) {
							e.setCancelled(true);
						}
						else if (e.getBlock().getType() == Material.TNT) {
							List<Location3D> list = new ArrayList<Location3D>();
							if (r.hasMetadata("tntBlocks")) {
								list = (List<Location3D>)r.getMetadata("tntBlocks");
							}
							list.add(Location3D.valueOf(e.getBlock().getLocation()));
							r.setMetadata("tntBlocks", list);
						}
						else {
							mg.getRollbackManager().logBlockChange(e.getBlockReplacedState().getBlock(), r.getArena());
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) {
		//Main.log.info("break: " + Bukkit.getWorlds().get(0).getTime() + "");
		for (Minigame mg : Minigame.getMinigameInstances()) {
			for (Round r : mg.getRoundList()) {
				if (r.isRollbackEnabled()) {
					if (r.getPlayers().containsKey(e.getPlayer().getName())) {
						if (!mg.getConfigManager().isBlockBreakAllowed()) {
							e.setCancelled(true);
						}
						else {
							mg.getRollbackManager().logBlockChange(e.getBlock(), r.getArena());
							//TODO: handle rollback of attached blocks
							for (int y = 1; e.getBlock().getY() + y < 256; y++) {
								Material type = e.getBlock().getLocation().add(0, y, 0).getBlock().getType();
								if (type == Material.SAND ||
										type == Material.GRAVEL ||
										type == Material.ANVIL ||
										type == Material.DRAGON_EGG) {
									mg.getRollbackManager().logBlockChange(
											e.getBlock().getLocation().add(0, y, 0).getBlock(), r.getArena()
									);
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBurn(BlockBurnEvent e) {
		boolean cancelled = false;
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()) {
			for (int i = 0; i < worlds.get(p).size(); i++) {
				if (worlds.get(p).get(i).equals(w)) {
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockBurnAllowed()) {
						e.setCancelled(true);
						cancelled = true;
						break;
					}
				}
			}
		}
		if (cancelled) {
			return;
		}
		Block adjBlock = MGUtil.getAttachedSign(e.getBlock());
		if (adjBlock != null) {
			for (Minigame mg : Minigame.getMinigameInstances()) {
				for (LobbySign l : mg.getLobbyManager().signs.values()) {
					if (l.getX() == adjBlock.getX() && l.getY() == adjBlock.getY() && l.getZ() == adjBlock.getZ() &&
							l.getWorld().equals(adjBlock.getWorld().getName())) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockFade(BlockFadeEvent e) {
		boolean cancelled = false;
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()) {
			for (int i = 0; i < worlds.get(p).size(); i++) {
				if (worlds.get(p).get(i).equals(w)) {
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockFadeAllowed()) {
						e.setCancelled(true);
						cancelled = true;
						break;
					}
				}
			}
		}
		if (cancelled) {
			return;
		}
		Block adjBlock = MGUtil.getAttachedSign(e.getBlock());
		if (adjBlock != null) {
			for (Minigame mg : Minigame.getMinigameInstances()) {
				for (LobbySign l : mg.getLobbyManager().signs.values()) {
					if (l.getX() == adjBlock.getX() && l.getY() == adjBlock.getY() && l.getZ() == adjBlock.getZ() &&
							l.getWorld().equals(adjBlock.getWorld().getName())) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockGrow(BlockGrowEvent e) {
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()) {
			for (int i = 0; i < worlds.get(p).size(); i++) {
				if (worlds.get(p).get(i).equals(w)) {
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockGrowAllowed()) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockIgnite(BlockIgniteEvent e) {
		boolean cancelled = false;
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()) {
			for (int i = 0; i < worlds.get(p).size(); i++) {
				if (worlds.get(p).get(i).equals(w)) {
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockIgniteAllowed()) {
						e.setCancelled(true);
						cancelled = true;
						break;
					}
				}
			}
		}
		if (cancelled) {
			return;
		}
		Block adjBlock = MGUtil.getAttachedSign(e.getBlock());
		if (adjBlock != null) {
			for (Minigame mg : Minigame.getMinigameInstances()) {
				for (LobbySign l : mg.getLobbyManager().signs.values()) {
					if (l.getX() == adjBlock.getX() && l.getY() == adjBlock.getY() && l.getZ() == adjBlock.getZ() &&
							l.getWorld().equals(adjBlock.getWorld().getName())) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockFlow(BlockFromToEvent e) {
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()) {
			for (int i = 0; i < worlds.get(p).size(); i++) {
				if (worlds.get(p).get(i).equals(w)) {
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockFlowAllowed()) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPhysics(BlockPhysicsEvent e) {
		boolean cancelled = false;
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()) {
			for (int i = 0; i < worlds.get(p).size(); i++) {
				if (worlds.get(p).get(i).equals(w)) {
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockPhysicsAllowed()) {
						e.setCancelled(true);
						cancelled = true;
						break;
					}
				}
			}
		}
		if (cancelled) {
			return;
		}
		Block adjBlock = MGUtil.getAttachedSign(e.getBlock());
		if (adjBlock != null) {
			for (Minigame mg : Minigame.getMinigameInstances()) {
				for (LobbySign l : mg.getLobbyManager().signs.values()) {
					if (l.getX() == adjBlock.getX() && l.getY() == adjBlock.getY() && l.getZ() == adjBlock.getZ() &&
							l.getWorld().equals(adjBlock.getWorld().getName())) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonExtend(BlockPistonExtendEvent e) {
		boolean cancelled = false;
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()) {
			for (int i = 0; i < worlds.get(p).size(); i++) {
				if (worlds.get(p).get(i).equals(w)) {
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockPistonAllowed()) {
						e.setCancelled(true);
						cancelled = true;
						break;
					}
				}
			}
		}
		if (cancelled) {
			return;
		}
		Block adjBlock = MGUtil.getAttachedSign(e.getBlock());
		if (adjBlock != null) {
			for (Minigame mg : Minigame.getMinigameInstances()) {
				for (LobbySign l : mg.getLobbyManager().signs.values()) {
					if (l.getX() == adjBlock.getX() && l.getY() == adjBlock.getY() && l.getZ() == adjBlock.getZ() &&
							l.getWorld().equals(adjBlock.getWorld().getName())) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonRetract(BlockPistonRetractEvent e) {
		boolean cancelled = false;
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()) {
			for (int i = 0; i < worlds.get(p).size(); i++) {
				if (worlds.get(p).get(i).equals(w)) {
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockPistonAllowed()) {
						e.setCancelled(true);
						cancelled = true;
						break;
					}
				}
			}
		}
		if (cancelled) {
			return;
		}
		Block adjBlock = MGUtil.getAttachedSign(e.getBlock());
		if (adjBlock != null) {
			for (Minigame mg : Minigame.getMinigameInstances()) {
				for (LobbySign l : mg.getLobbyManager().signs.values()) {
					if (l.getX() == adjBlock.getX() && l.getY() == adjBlock.getY() && l.getZ() == adjBlock.getZ() &&
							l.getWorld().equals(adjBlock.getWorld().getName())) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockSpread(BlockSpreadEvent e) {
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()) {
			for (int i = 0; i < worlds.get(p).size(); i++) {
				if (worlds.get(p).get(i).equals(w)) {
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockSpreadAllowed()) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getBlock().getState() instanceof Sign) { // just in case
			for (Minigame mg : Minigame.getMinigameInstances()) { // iterate registered minigames
				System.out.println(e.getLine(0) + ", " + mg.getConfigManager().getSignId());
				String[] lines = {
						ChatColor.stripColor(e.getLine(0)),
						ChatColor.stripColor(e.getLine(1)),
						ChatColor.stripColor(e.getLine(2)),
						ChatColor.stripColor(e.getLine(3))};
				if (lines[0].equalsIgnoreCase(mg.getConfigManager().getSignId())) { // it's a lobby sign-to-be
					if (e.getPlayer().hasPermission(mg.getPlugin().getName() + ".lobby.create")) {
						// make sure last line (sign index) is a number if it's a player sign
						if (!lines[1].equalsIgnoreCase("players") || MGUtil.isInteger(lines[3])) {
							try {
								int index = MGUtil.isInteger(lines[3]) ? Integer.parseInt(lines[3]) : 0;
								mg.getLobbyManager().add(e.getBlock().getLocation(), lines[2],
										LobbyType.fromString(lines[1]), index);
							}
							catch (NoSuchArenaException ex) {
								e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("arena.alert.dne"));
							}
							catch (IllegalArgumentException ex) {
								if (ex.getMessage().contains("index")) {
									e.getPlayer().sendMessage(ChatColor.RED +
											locale.getMessage("lobby.alert.invalid-index"));
								}
								else if (ex.getMessage()
										.contains(Main.locale.getMessage("plugin.alert.invalid-string"))) {
									e.getPlayer().sendMessage(ChatColor.RED +
											locale.getMessage("lobby.alert.invalid-type"));
								}
								else {
									ex.printStackTrace();
								}
							}
							catch (InvalidLocationException ex) {
								e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("lobby.alert.no-sign"));
							}
						}
						else {
							e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("lobby.alert.invalid-index"));
						}
					}
					break;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		for (Minigame mg : Minigame.getMinigameInstances()) {
			MGPlayer p = mg.getMGPlayer(e.getPlayer().getName());
			if (p != null) {
				if (p.isSpectating()) {
					e.setCancelled(true);
					return;
				}
			}
		}
		if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (e.getClickedBlock().getState() instanceof Sign) {
				for (Minigame mg : Minigame.getMinigameInstances()) {
					LobbySign ls = mg.getLobbyManager().getSign(Location3D.valueOf(e.getClickedBlock().getLocation()));
					if (ls != null) {
						e.setCancelled(true);
						if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getPlayer().isSneaking()) {
							if (e.getPlayer().hasPermission(ls.getPlugin() + ".lobby.destroy")) {
								e.setCancelled(false);
								ls.remove();
								return;
							}
						}
						Round r = mg.getRound(ls.getArena());
						if (r == null) {
							try {
								r = mg.createRound(ls.getArena());
							}
							catch (NoSuchArenaException ex) {
								e.getPlayer().sendMessage(ChatColor.RED +
										locale.getMessage("error.personal.load-fail").replace("%", ls.getArena()));
								return;
							}
						}
						try {
							JoinResult result = r.addPlayer(e.getPlayer().getName());
							MGUtil.callEvent(new LobbyClickEvent(e.getPlayer().getName(), r, ls, result));
						}
						catch (PlayerOfflineException ex) { // this can never happen
							ex.printStackTrace();
						}
						catch (PlayerPresentException ex) {
							e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("alert.personal.in-round"));
						}
						catch (RoundFullException ex) {
							e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("alert.personal.round-full"));
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		if (e.getMessage().startsWith("kit")) {
			for (Minigame mg : Minigame.getMinigameInstances()) {
				if (mg.isPlayer(e.getPlayer().getName())) {
					if (!mg.getConfigManager().areKitsAllowed()) {
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("alert.personal.kits"));
					}
				}
			}
		}
		else if (e.getMessage().startsWith("msg ") ||
				e.getMessage().startsWith("tell ") ||
				e.getMessage().startsWith("r ") ||
				e.getMessage().startsWith("me ")) {
			for (Minigame mg : Minigame.getMinigameInstances()) {
				if (mg.isPlayer(e.getPlayer().getName())) {
					if (!mg.getConfigManager().arePMsAllowed()) {
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("alert.personal.pm"));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerHungerEvent(FoodLevelChangeEvent e) {
		if (e.getEntityType() == EntityType.PLAYER) {
			for (Minigame mg : Minigame.getMinigameInstances()) {
				if (!mg.getConfigManager().isHungerEnabled() && mg.isPlayer((e.getEntity()).getName())) {
					e.setCancelled(true);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e) {
		String w = e.getEntity().getWorld().getName();
		for (String p : worlds.keySet()) {
			for (int i = 0; i < worlds.get(p).size(); i++) {
				if (worlds.get(p).get(i).equals(w)) {
					if (!Minigame.getMinigameInstance(p).getConfigManager().isEntityExplosionsAllowed()) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}
		for (Minigame mg : Minigame.getMinigameInstances()) {
			for (Round r : mg.getRoundList()) {
				if (r.hasMetadata("tntBlocks")) {
					List<Location3D> list = (List<Location3D>)r.getMetadata("tntBlocks");
					if (list.contains(new Location3D(
							e.getLocation().getBlockX(),
							e.getLocation().getBlockY(),
							e.getLocation().getBlockZ()
					))) {
						for (Block b : e.blockList()) {
							mg.getRollbackManager().logBlockChange(b, r.getArena());
						}
						mg.getRollbackManager().logBlockChange(e.getLocation().getBlock(), r.getArena());
						break;
					}
				}
			}
		}
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		List<Player> remove = new ArrayList<Player>();
		for (Minigame mg : Minigame.getMinigameInstances()) {
			if (mg.getConfigManager().isPerRoundChatEnabled()) {
				MGPlayer sender = mg.getMGPlayer(e.getPlayer().getName());
				for (Player pl : e.getRecipients()) {
					MGPlayer recipient = mg.getMGPlayer(pl.getName());
					if ((sender == null) != (recipient == null)) {
						remove.add(pl);
					}
					else if (sender != null && recipient != null) {
						if (!sender.getRound().getArena().equals(recipient.getRound().getArena())) {
							remove.add(pl);
						}
						else if (mg.getConfigManager().isTeamChatEnabled() &&
								(sender.getTeam() != null &&
										!sender.getTeam().equals(recipient.getTeam()))) {
							remove.add(pl);
						}
						else if (mg.getConfigManager().isSpectatorChatSeparate() &&
								sender.isSpectating() &&
								!recipient.isSpectating()) {
							remove.add(pl);
						}
					}
				}
			}
		}
		e.getRecipients().removeAll(remove);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		String w = e.getEntity().getWorld().getName();
		for (String p : worlds.keySet()) {
			for (int i = 0; i < worlds.get(p).size(); i++) {
				if (worlds.get(p).get(i).equals(w)) {
					if (!Minigame.getMinigameInstance(p).getConfigManager().isMobSpawningAllowed()) {
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityTarget(EntityTargetEvent e) {
		if (e.getTarget() != null && e.getTarget().getType() == EntityType.PLAYER) {
			for (Minigame mg : Minigame.getMinigameInstances()) {
				MGPlayer mp = mg.getMGPlayer(((Player)e.getTarget()).getName());
				if (mp != null && (!mg.getConfigManager().isEntityTargetingEnabled() || mp.isSpectating())) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingBreak(HangingBreakByEntityEvent e) {
		if (e.getRemover() instanceof Player ||
				(e.getRemover() instanceof Projectile &&
						((Projectile)e.getRemover()).getShooter() instanceof Player)) {
			for (Minigame mg : Minigame.getMinigameInstances()) {
				if (!mg.getConfigManager().isHangingBreakAllowed() && mg.isPlayer(e.getRemover() instanceof Player ?
						((Player)e.getRemover()).getName() :
						((Player)((Projectile)e.getRemover()).getShooter()).getName())) {
					e.setCancelled(true);
				}
			}
		}
	}
}
