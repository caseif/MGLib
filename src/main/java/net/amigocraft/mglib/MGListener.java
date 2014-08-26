package net.amigocraft.mglib;

import net.amigocraft.mglib.api.*;
import net.amigocraft.mglib.event.player.MGPlayerDeathEvent;
import net.amigocraft.mglib.event.round.LobbyClickEvent;
import net.amigocraft.mglib.exception.*;
import net.amigocraft.mglib.misc.JoinResult;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static net.amigocraft.mglib.Main.locale;

class MGListener implements Listener {

	static HashMap<String, List<String>> worlds = new HashMap<String, List<String>>();

	static void initialize(){
		for (Minigame mg : Minigame.getMinigameInstances()){
			MGListener.addWorlds(mg.getPlugin().getName());
		}
	}

	static void addWorlds(String plugin){
		File f = new File(Bukkit.getPluginManager().getPlugin(plugin).getDataFolder(), "arenas.yml");
		if (f.exists()){
			YamlConfiguration y = new YamlConfiguration();
			try {
				List<String> worldList = new ArrayList<String>();
				y.load(f);
				for (String k : y.getKeys(false)){
					worldList.add(k);
				}
				worlds.put(plugin, worldList);
			}
			catch (Exception ex){
				ex.printStackTrace();
				Main.log.severe("An exception occurred while loading world list for plugin " + plugin);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST) // so that we can prepare everything for the hooking plugins
	public void onPlayerQuit(PlayerQuitEvent e){
		final String p = e.getPlayer().getName();
		for (Minigame mg : Minigame.getMinigameInstances()){
			for (Round r : mg.getRoundList()){
				MGPlayer mp = r.getMGPlayer(p);
				if (mp != null){
					try {
						mp.removeFromRound();
						if (!p.equalsIgnoreCase("Testing123")){ // it won't break when I'm testing, but offline servers still get screwed up
							String pUUID = UUIDFetcher.getUUIDOf(p).toString();
							UUIDFetcher.removeUUID(p);
							YamlConfiguration y = new YamlConfiguration();
							File f = new File(Main.plugin.getDataFolder(), "offlineplayers.yml");
							if (!f.exists()){
								f.createNewFile();
							}
							y.load(f);
							Location el = mg.getConfigManager().getDefaultExitLocation();
							y.set(pUUID + ".w", el.getWorld().getName());
							y.set(pUUID + ".x", el.getX());
							y.set(pUUID + ".y", el.getY());
							y.set(pUUID + ".z", el.getZ());
							y.save(f);
						}
					}
					catch (Exception ex){
						ex.printStackTrace();
						Main.log.severe("An exception occurred while saving data for " + p);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent e){
		Player pl = null;
		Player p2 = null; // don't ask why it's named like this
		if (e.getEntity() instanceof Player){
			p2 = (Player) e.getEntity();
		}
		if (e instanceof EntityDamageByEntityEvent){
			Entity damager = ((EntityDamageByEntityEvent) e).getDamager();
			if (damager instanceof Player) // damager is a player
			{
				pl = (Player) damager;
			}
			else if (damager instanceof Projectile) // damager is an arrow or something
			{
				if (((Projectile) damager).getShooter() instanceof Player){
					pl = (Player) ((Projectile) damager).getShooter(); // a player shot the projectile (e.g. an arrow from a bow)
				}
			}

			//TODO: probably rewrite this bit at some point
			if (pl != null || p2 != null){
				for (Minigame mg : Minigame.getMinigameInstances()){
					if (p2 != null){
						MGPlayer p = mg.getMGPlayer(p2.getName());
						if (p != null){
							if (p.isSpectating() || !p.getRound().isDamageAllowed()){
								e.setCancelled(true); // we don't want any spooky ghosts being harassed by the living
								return;
							}
						}
					}
					if (pl != null){
						MGPlayer p = mg.getMGPlayer(pl.getName());
						if (p != null && (p.isSpectating() || !p.getRound().isPvPAllowed())){
							e.setCancelled(true); // we don't want any spooky ghosts meddling in the affairs of the living
							return;
						}
						else if (p != null && !mg.getConfigManager().isItemFrameDamageAllowed() && e.getEntity() instanceof ItemFrame){
							e.setCancelled(true);
							return;
						}
					}
					if (pl != null && p2 != null){
						MGPlayer m1 = mg.getMGPlayer(pl.getName());
						MGPlayer m2 = mg.getMGPlayer(p2.getName());
						if (m1 != null && m2 != null && !mg.getConfigManager().isTeamDamageAllowed() &&
								m1.getTeam() != null && m2.getTeam() != null && m1.getTeam().equalsIgnoreCase(m2.getTeam())){
							e.setCancelled(true);
							return;
						}
					}
				}
			}
		}
		for (Minigame mg : Minigame.getMinigameInstances()){
			if (p2 != null){
				MGPlayer p = mg.getMGPlayer(p2.getName());
				if (p != null && p.isSpectating()){
					e.setCancelled(true);
					return;
				}
				/*if (p != null && p.getRound() != null && p.getRound().getConfigManager().isOverrideDeathEvent()){
					// override the death event with a custom one
					double actualDamage = 0;
					boolean force = false;
					try {
						e.getClass().getMethod("getFinalDamage", new Class<?>[]{}); // test for new damage API
						actualDamage = e.getFinalDamage();
					}
					catch (NoSuchMethodException ex){ // no support for new damage API so we need to guesstimate it ourselves
						int armor = 0;
						// calculate armor-based damage reduction
						if (e.getCause() == DamageCause.ENTITY_ATTACK ||
								e.getCause() == DamageCause.PROJECTILE ||
								e.getCause() == DamageCause.FIRE ||
								e.getCause() == DamageCause.BLOCK_EXPLOSION || 
								e.getCause() == DamageCause.CONTACT ||
								e.getCause() == DamageCause.LAVA ||
								e.getCause() == DamageCause.ENTITY_EXPLOSION ||
								e.getCause() == DamageCause.LIGHTNING){
							HashMap<Material, Integer> protection = new HashMap<Material, Integer>();
							protection.put(Material.LEATHER_HELMET, 1);
							protection.put(Material.LEATHER_CHESTPLATE, 3);
							protection.put(Material.LEATHER_LEGGINGS, 2);
							protection.put(Material.LEATHER_BOOTS, 1);
							protection.put(Material.IRON_HELMET, 2);
							protection.put(Material.IRON_CHESTPLATE, 5);
							protection.put(Material.IRON_LEGGINGS, 3);
							protection.put(Material.IRON_BOOTS, 1);
							protection.put(Material.CHAINMAIL_HELMET, 2);
							protection.put(Material.CHAINMAIL_CHESTPLATE, 5);
							protection.put(Material.CHAINMAIL_LEGGINGS, 3);
							protection.put(Material.CHAINMAIL_BOOTS, 1);
							protection.put(Material.GOLD_HELMET, 2);
							protection.put(Material.GOLD_CHESTPLATE, 6);
							protection.put(Material.GOLD_LEGGINGS, 5);
							protection.put(Material.GOLD_BOOTS, 2);
							protection.put(Material.DIAMOND_HELMET, 3);
							protection.put(Material.DIAMOND_CHESTPLATE, 8);
							protection.put(Material.DIAMOND_LEGGINGS, 6);
							protection.put(Material.DIAMOND_BOOTS, 3);
							if (p2.getInventory().getArmorContents()[0] != null)
								if (protection.containsKey(p2.getInventory().getArmorContents()[0].getType()))
									armor += protection.get(p2.getInventory().getArmorContents()[0].getType());
							if (p2.getInventory().getArmorContents()[1] != null)
								if (protection.containsKey(p2.getInventory().getArmorContents()[1].getType()))
									armor += protection.get(p2.getInventory().getArmorContents()[1].getType());
							if (p2.getInventory().getArmorContents()[2] != null)
								if (protection.containsKey(p2.getInventory().getArmorContents()[2].getType()))
									armor += protection.get(p2.getInventory().getArmorContents()[2].getType());
							if (p2.getInventory().getArmorContents()[3] != null)
								if (protection.containsKey(p2.getInventory().getArmorContents()[3].getType()))
									armor += protection.get(p2.getInventory().getArmorContents()[3].getType());
						}
						double armorMod = armor * .04 * e.getDamage(); // armor-based damage reduction
						double enchantMod = 0;
						// calculate enchantment-based damage reduction
						for (ItemStack a : p2.getInventory().getArmorContents()){
							for (Enchantment en : a.getEnchantments().keySet()){
								if (en == Enchantment.PROTECTION_ENVIRONMENTAL)
									enchantMod += Math.floor((6 + Math.pow(a.getEnchantmentLevel(en), 2)) * 0.75 / 3);
								else if (en == Enchantment.PROTECTION_EXPLOSIONS &&
										e.getCause() == DamageCause.BLOCK_EXPLOSION || e.getCause() == DamageCause.ENTITY_EXPLOSION)
									enchantMod += Math.floor((6 + Math.pow(a.getEnchantmentLevel(en), 2)) * 1.5 / 3);
								else if (en == Enchantment.PROTECTION_FALL &&
										e.getCause() == DamageCause.FALL)
									enchantMod += Math.floor((6 + Math.pow(a.getEnchantmentLevel(en), 2)) * 2.5 / 3);
								else if (en == Enchantment.PROTECTION_FIRE &&
										e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.FIRE_TICK || e.getCause() == DamageCause.LAVA ||
										(e.getCause() == DamageCause.PROJECTILE && e instanceof EntityDamageByEntityEvent && // extra reassurance
										((EntityDamageByEntityEvent)e).getDamager().getType() == EntityType.FIREBALL))
									enchantMod += Math.floor((6 + Math.pow(a.getEnchantmentLevel(en), 2)) * 1.25 / 3);
								else if (en == Enchantment.PROTECTION_PROJECTILE &&
										e.getCause() == DamageCause.PROJECTILE)
									enchantMod += Math.floor((6 + Math.pow(a.getEnchantmentLevel(en), 2)) * 1.5 / 3);
							}
						}
						enchantMod = Math.max(Math.ceil(Math.max(enchantMod, 25) * 0.75f), 20) * 0.04 * e.getDamage(); // this is how MC calculates it
						double potionMod = 1;
						if (e.getCause() != DamageCause.VOID)
							for (PotionEffect pe : p2.getActivePotionEffects())
								if (pe.getType() == PotionEffectType.DAMAGE_RESISTANCE)
									potionMod = (float)(25 - ((pe.getAmplifier() + 1) * 5)) / 25.0f;
						actualDamage = (int)(e.getDamage() * potionMod - armorMod - enchantMod);
						force = true;
					}
					Main.log("damage: " + p2.getWorld().getTime() + ", " +
							p2.getName() + ", " + actualDamage + ", " + ((Player)e.getEntity()).getHealth(), LogLevel.DEBUG);
					if (actualDamage >= ((Player)e.getEntity()).getHealth()){
						e.setCancelled(true);
						MGUtil.callEvent(new MGPlayerDeathEvent(p, e.getCause(),
								e instanceof EntityDamageByEntityEvent ?
										((EntityDamageByEntityEvent)e).getDamager() instanceof Projectile ?
												(Entity)((Projectile)((EntityDamageByEntityEvent)e).getDamager()).getShooter() :
													((EntityDamageByEntityEvent)e).getDamager() :
														null));
					}
					else if (mg.getConfigManager().isForcePreciseDamage() && force){
						e.setCancelled(true);
						p2.setHealth(p2.getHealth() - actualDamage);
						p2.getWorld().playSound(p2.getLocation(), Sound.HURT_FLESH, 10, 1);
						MGUtil.damage(p2);
					}
					break;
				}*/
			}
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		for (Minigame mg : Minigame.getMinigameInstances()){
			if (mg.getConfigManager().isOverrideDeathEvent() && mg.isPlayer(e.getEntity().getName())){
				e.setDeathMessage(null);
				e.setKeepLevel(true);
				e.getDrops().clear();
				try {
					// oh God why did I think this was a good idea
					Class<?> packetClass;
					Object packet;
					try {
						packetClass = MGUtil.getMCClass("PacketPlayInClientCommand");
						packet = packetClass.getConstructor(MGUtil.getMCClass("EnumClientCommand")).newInstance(Enum.valueOf((Class<? extends Enum>) MGUtil.getMCClass("EnumClientCommand"), "PERFORM_RESPAWN"));
					}
					catch (Exception ex){
						packetClass = MGUtil.getMCClass("Packet205ClientCommand");
						packet = packetClass.getConstructor().newInstance();
						packetClass.getDeclaredField("a").set(packet, 1);
					}
					Object nmsPlayer = MGUtil.getCraftClass("entity.CraftPlayer").getMethod("getHandle").invoke(e.getEntity());
					Object conn = nmsPlayer.getClass().getDeclaredField("playerConnection").get(nmsPlayer);
					conn.getClass().getMethod("a", packetClass).invoke(conn, packet);
				}
				catch (Exception ex){
					ex.printStackTrace();
				}
				EntityDamageEvent ed = e.getEntity().getLastDamageCause();
				MGUtil.callEvent(new MGPlayerDeathEvent(mg.getMGPlayer(e.getEntity().getName()), ed.getCause(), ed instanceof EntityDamageByEntityEvent ? ((EntityDamageByEntityEvent) ed).getDamager() instanceof Projectile ? (Entity) ((Projectile) ((EntityDamageByEntityEvent) ed).getDamager()).getShooter() : ((EntityDamageByEntityEvent) ed).getDamager() : null));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e){
		for (Minigame mg : Minigame.getMinigameInstances()){
			if (mg.getConfigManager().isOverrideDeathEvent() && mg.isPlayer(e.getPlayer().getName())){
				e.setRespawnLocation(e.getPlayer().getLocation());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST) // so that we can prepare everything for the hooking plugins
	public void onPlayerJoin(PlayerJoinEvent e){
		final String p = e.getPlayer().getName();
		try {
			UUIDFetcher.addUUID(p, UUIDFetcher.getUUIDOf(p));
		}
		catch (Exception ex){
			ex.printStackTrace();
			Main.log.severe("Failed to fetch UUID for player " + p);
		}
		try {
			YamlConfiguration y = new YamlConfiguration();
			File f = new File(Main.plugin.getDataFolder(), "offlineplayers.yml");
			if (!f.exists()){
				f.createNewFile();
			}
			y.load(f);
			String pUUID = UUIDFetcher.getUUIDOf(p).toString();
			if (y.isSet(pUUID)){
				final String ww = y.getString(pUUID + ".w");
				final double xx = y.getDouble(pUUID + ".x");
				final double yy = y.getDouble(pUUID + ".y");
				final double zz = y.getDouble(pUUID + ".z");
				MGPlayer mp = new MGPlayer("MGLib", p, "null");
				mp.reset(new Location(Bukkit.getWorld(ww), xx, yy, zz));
				y.set(pUUID, null);
				y.save(f);
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
			Main.log.severe("An exception occurred while loading data for " + p);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent e){
		boolean found = false;
		for (Minigame mg : Minigame.getMinigameInstances()){
			for (Round r : mg.getRoundList()){
				MGPlayer p = r.getMGPlayer(e.getPlayer().getName());
				if (p != null){
					Location l = e.getTo();
					if (!l.getWorld().getName().equals(r.getWorld())){
						found = true;
						try {
							p.removeFromRound(l);
						}
						catch (NoSuchPlayerException ex){ // this can never happen
							ex.printStackTrace();
						}
						catch (PlayerOfflineException ex){ // this can definitely never happen
							ex.printStackTrace();
						}
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
								found = true;
								try {
									p.removeFromRound(l);
								}
								catch (NoSuchPlayerException ex){ // this can never happen
									ex.printStackTrace();
								}
								catch (PlayerOfflineException ex){ // this can definitely never happen
									ex.printStackTrace();
								}
							}
						}
					}
					break;
				}
			}
			if (found){
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent e){
		for (Minigame mg : Minigame.getMinigameInstances()){
			MGPlayer mp = mg.getMGPlayer(e.getWhoClicked().getName());
			if (mp != null){
				if (mp.isSpectating()){
					e.setCancelled(true);
					return;
				}
				if (e.getInventory().getHolder() instanceof BlockState){
					mg.getRollbackManager().logInventoryChange(e.getInventory(), ((BlockState) e.getInventory().getHolder()).getBlock(), mp.getArena());
					return;
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent e){
		for (Minigame mg : Minigame.getMinigameInstances()){
			for (Round r : mg.getRoundList()){
				if (r.isRollbackEnabled()){
					if (r.getPlayers().containsKey(e.getPlayer().getName())){
						if (!mg.getConfigManager().isBlockPlaceAllowed()){
							e.setCancelled(true);
						}
						else if (e.getBlock().getType() == Material.TNT){
							List<Location3D> list = new ArrayList<Location3D>();
							if (r.hasMetadata("tntBlocks")){
								list = (List<Location3D>) r.getMetadata("tntBlocks");
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
	public void onBlockBreak(BlockBreakEvent e){
		//Main.log.info("break: " + Bukkit.getWorlds().get(0).getTime() + "");
		for (Minigame mg : Minigame.getMinigameInstances()){
			for (Round r : mg.getRoundList()){
				if (r.isRollbackEnabled()){
					if (r.getPlayers().containsKey(e.getPlayer().getName())){
						if (!mg.getConfigManager().isBlockBreakAllowed()){
							e.setCancelled(true);
						}
						else {
							mg.getRollbackManager().logBlockChange(e.getBlock(), r.getArena());
							//TODO: handle rollback of attached blocks
							for (int y = 1; e.getBlock().getY() + y < 256; y++){
								Material type = e.getBlock().getLocation().add(0, y, 0).getBlock().getType();
								if (type == Material.SAND || type == Material.GRAVEL || type == Material.ANVIL || type == Material.DRAGON_EGG){
									mg.getRollbackManager().logBlockChange(e.getBlock().getLocation().add(0, y, 0).getBlock(), r.getArena());
								}
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBurn(BlockBurnEvent e){
		boolean cancelled = false;
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockBurnAllowed()){
						e.setCancelled(true);
						cancelled = true;
						break;
					}
				}
			}
		}
		if (cancelled){
			return;
		}
		Block adjBlock = MGUtil.getAttachedSign(e.getBlock());
		if (adjBlock != null){
			for (Minigame mg : Minigame.getMinigameInstances()){
				for (LobbySign l : mg.getLobbyManager().signs.values()){
					if (l.getX() == adjBlock.getX() && l.getY() == adjBlock.getY() && l.getZ() == adjBlock.getZ() &&
							l.getWorld().equals(adjBlock.getWorld().getName())){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockFade(BlockFadeEvent e){
		boolean cancelled = false;
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockFadeAllowed()){
						e.setCancelled(true);
						cancelled = true;
						break;
					}
				}
			}
		}
		if (cancelled){
			return;
		}
		Block adjBlock = MGUtil.getAttachedSign(e.getBlock());
		if (adjBlock != null){
			for (Minigame mg : Minigame.getMinigameInstances()){
				for (LobbySign l : mg.getLobbyManager().signs.values()){
					if (l.getX() == adjBlock.getX() && l.getY() == adjBlock.getY() && l.getZ() == adjBlock.getZ() &&
							l.getWorld().equals(adjBlock.getWorld().getName())){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockGrow(BlockGrowEvent e){
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockGrowAllowed()){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockIgnite(BlockIgniteEvent e){
		boolean cancelled = false;
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockIgniteAllowed()){
						e.setCancelled(true);
						cancelled = true;
						break;
					}
				}
			}
		}
		if (cancelled){
			return;
		}
		Block adjBlock = MGUtil.getAttachedSign(e.getBlock());
		if (adjBlock != null){
			for (Minigame mg : Minigame.getMinigameInstances()){
				for (LobbySign l : mg.getLobbyManager().signs.values()){
					if (l.getX() == adjBlock.getX() && l.getY() == adjBlock.getY() && l.getZ() == adjBlock.getZ() &&
							l.getWorld().equals(adjBlock.getWorld().getName())){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockFlow(BlockFromToEvent e){
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockFlowAllowed()){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPhysics(BlockPhysicsEvent e){
		boolean cancelled = false;
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockPhysicsAllowed()){
						e.setCancelled(true);
						cancelled = true;
						break;
					}
				}
			}
		}
		if (cancelled){
			return;
		}
		Block adjBlock = MGUtil.getAttachedSign(e.getBlock());
		if (adjBlock != null){
			for (Minigame mg : Minigame.getMinigameInstances()){
				for (LobbySign l : mg.getLobbyManager().signs.values()){
					if (l.getX() == adjBlock.getX() && l.getY() == adjBlock.getY() && l.getZ() == adjBlock.getZ() &&
							l.getWorld().equals(adjBlock.getWorld().getName())){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonExtend(BlockPistonExtendEvent e){
		boolean cancelled = false;
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockPistonAllowed()){
						e.setCancelled(true);
						cancelled = true;
						break;
					}
				}
			}
		}
		if (cancelled){
			return;
		}
		Block adjBlock = MGUtil.getAttachedSign(e.getBlock());
		if (adjBlock != null){
			for (Minigame mg : Minigame.getMinigameInstances()){
				for (LobbySign l : mg.getLobbyManager().signs.values()){
					if (l.getX() == adjBlock.getX() && l.getY() == adjBlock.getY() && l.getZ() == adjBlock.getZ() &&
							l.getWorld().equals(adjBlock.getWorld().getName())){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPistonRetract(BlockPistonRetractEvent e){
		boolean cancelled = false;
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockPistonAllowed()){
						e.setCancelled(true);
						cancelled = true;
						break;
					}
				}
			}
		}
		if (cancelled){
			return;
		}
		Block adjBlock = MGUtil.getAttachedSign(e.getBlock());
		if (adjBlock != null){
			for (Minigame mg : Minigame.getMinigameInstances()){
				for (LobbySign l : mg.getLobbyManager().signs.values()){
					if (l.getX() == adjBlock.getX() && l.getY() == adjBlock.getY() && l.getZ() == adjBlock.getZ() &&
							l.getWorld().equals(adjBlock.getWorld().getName())){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockSpread(BlockSpreadEvent e){
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockSpreadAllowed()){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) throws IndexOutOfBoundsException, InvalidLocationException{
		if (e.getBlock().getState() instanceof Sign){ // just in case
			for (Minigame mg : Minigame.getMinigameInstances()){ // iterate registered minigames
				if (e.getLine(0).equalsIgnoreCase(mg.getConfigManager().getSignId())){ // it's a lobby sign-to-be
					if (e.getPlayer().hasPermission(mg.getPlugin().getName() + ".lobby.create")){
						if (!e.getLine(1).equalsIgnoreCase("players") || MGUtil.isInteger(e.getLine(3))){ // make sure last line (sign index) is a number if it's a player sign
							try {
								int index = MGUtil.isInteger(e.getLine(3)) ? Integer.parseInt(e.getLine(3)) : 0;
								mg.getLobbyManager().add(e.getBlock().getLocation(), e.getLine(2), LobbyType.fromString(e.getLine(1)), index);
							}
							catch (NoSuchArenaException ex){
								e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("arena-not-exists"));
							}
							catch (IllegalArgumentException ex){
								if (ex.getMessage().contains("index")){
									e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("invalid-sign-index"));
								}
								else if (ex.getMessage().contains("Invalid string!")){
									e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("invalid-sign-type"));
								}
								else {
									ex.printStackTrace();
								}
							}
						}
						else {
							e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("invalid-sign-index"));
						}
					}
					break;
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e){
		for (Minigame mg : Minigame.getMinigameInstances()){
			MGPlayer p = mg.getMGPlayer(e.getPlayer().getName());
			if (p != null){
				if (p.isSpectating()){
					e.setCancelled(true);
					return;
				}
			}
		}
		if (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if (e.getClickedBlock().getState() instanceof Sign){
				for (Minigame mg : Minigame.getMinigameInstances()){
					LobbySign ls = mg.getLobbyManager().getSign(Location3D.valueOf(e.getClickedBlock().getLocation()));
					if (ls != null){
						e.setCancelled(true);
						if (e.getAction() == Action.LEFT_CLICK_BLOCK && e.getPlayer().isSneaking()){
							if (e.getPlayer().hasPermission(ls.getPlugin() + ".lobby.destroy")){
								e.setCancelled(false);
								ls.remove();
								return;
							}
						}
						Round r = mg.getRound(ls.getArena());
						if (r == null){
							try {
								r = mg.createRound(ls.getArena());
							}
							catch (NoSuchArenaException ex){
								e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("arena-load-fail").replace("%", ls.getArena()));
								return;
							}
						}
						try {
							JoinResult result = r.addPlayer(e.getPlayer().getName());
							MGUtil.callEvent(new LobbyClickEvent(e.getPlayer().getName(), r, ls, result));
						}
						catch (PlayerOfflineException ex){ // this can never happen
							ex.printStackTrace();
						}
						catch (PlayerPresentException ex){
							e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("in-round"));
						}
						catch (RoundFullException ex){
							e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("round-full"));
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e){
		if (e.getMessage().startsWith("kit")){
			for (Minigame mg : Minigame.getMinigameInstances()){
				if (mg.isPlayer(e.getPlayer().getName())){
					if (!mg.getConfigManager().areKitsAllowed()){
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("no-kits"));
					}
				}
			}
		}
		else if (e.getMessage().startsWith("msg") || e.getMessage().startsWith("tell") || e.getMessage().startsWith("r ") ||
				e.getMessage().startsWith("me")){
			for (Minigame mg : Minigame.getMinigameInstances()){
				if (mg.isPlayer(e.getPlayer().getName())){
					if (!mg.getConfigManager().arePMsAllowed()){
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("no-pms"));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerHungerEvent(FoodLevelChangeEvent e){
		if (e.getEntityType() == EntityType.PLAYER){
			for (Minigame mg : Minigame.getMinigameInstances()){
				if (!mg.getConfigManager().isHungerEnabled() && mg.isPlayer((e.getEntity()).getName())){
					e.setCancelled(true);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e){
		String w = e.getEntity().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isEntityExplosionsAllowed()){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
		for (Minigame mg : Minigame.getMinigameInstances()){
			for (Round r : mg.getRoundList()){
				if (r.hasMetadata("tntBlocks")){
					List<Location3D> list = (List<Location3D>) r.getMetadata("tntBlocks");
					if (list.contains(new Location3D(e.getLocation().getBlockX(), e.getLocation().getBlockY(), e.getLocation().getBlockZ()))){
						for (Block b : e.blockList()){
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
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e){
		List<Player> remove = new ArrayList<Player>();
		for (Minigame mg : Minigame.getMinigameInstances()){
			if (mg.getConfigManager().isPerRoundChatEnabled()){
				MGPlayer sender = mg.getMGPlayer(e.getPlayer().getName());
				for (Player pl : e.getRecipients()){
					MGPlayer recipient = mg.getMGPlayer(pl.getName());
					if ((sender == null) != (recipient == null)){
						remove.add(pl);
					}
					else if (sender != null && recipient != null){
						if (!sender.getRound().getArena().equals(recipient.getRound().getArena())){
							remove.add(pl);
						}
						else if (mg.getConfigManager().isTeamChatEnabled() && (sender.getTeam() != null && !sender.getTeam().equals(recipient.getTeam()))){
							remove.add(pl);
						}
						else if (mg.getConfigManager().isSpectatorChatSeparate() && sender.isSpectating() && !recipient.isSpectating()){
							remove.add(pl);
						}
					}
				}
			}
		}
		e.getRecipients().removeAll(remove);
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e){
		String w = e.getEntity().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isMobSpawningAllowed()){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityTarget(EntityTargetEvent e){
		if (e.getTarget() != null && e.getTarget().getType() == EntityType.PLAYER){
			for (Minigame mg : Minigame.getMinigameInstances()){
				MGPlayer mp = mg.getMGPlayer(((Player) e.getTarget()).getName());
				if (mp != null && (!mg.getConfigManager().isEntityTargetingEnabled() || mp.isSpectating())){
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingBreak(HangingBreakByEntityEvent e){
		if (e.getRemover() instanceof Player || (e.getRemover() instanceof Projectile && ((Projectile) e.getRemover()).getShooter() instanceof Player)){
			for (Minigame mg : Minigame.getMinigameInstances()){
				if (!mg.getConfigManager().isHangingBreakAllowed() && mg.isPlayer(e.getRemover() instanceof Player ? ((Player) e.getRemover()).getName() : ((Player) ((Projectile) e.getRemover()).getShooter()).getName())){
					e.setCancelled(true);
				}
			}
		}
	}

}
