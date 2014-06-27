package net.amigocraft.mglib;

import static net.amigocraft.mglib.Main.locale;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.amigocraft.mglib.api.LobbySign;
import net.amigocraft.mglib.api.LobbyType;
import net.amigocraft.mglib.api.MGPlayer;
import net.amigocraft.mglib.api.Minigame;
import net.amigocraft.mglib.api.Round;
import net.amigocraft.mglib.event.player.LobbyClickEvent;
import net.amigocraft.mglib.event.player.MGPlayerDeathEvent;
import net.amigocraft.mglib.event.round.MGRoundEvent;
import net.amigocraft.mglib.exception.ArenaNotExistsException;
import net.amigocraft.mglib.exception.InvalidLocationException;
import net.amigocraft.mglib.exception.PlayerNotPresentException;
import net.amigocraft.mglib.exception.PlayerOfflineException;
import net.amigocraft.mglib.exception.PlayerPresentException;
import net.amigocraft.mglib.exception.RoundFullException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class MGListener implements Listener {

	static HashMap<String, List<String>> worlds = new HashMap<String, List<String>>();

	static void initialize(){
		for (Minigame mg : Minigame.getMinigameInstances())
			MGListener.addWorlds(mg.getPlugin());
	}

	static void addWorlds(JavaPlugin plugin){
		File f = new File(plugin.getDataFolder(), "arenas.yml");
		if (f.exists()){
			YamlConfiguration y = new YamlConfiguration();
			try {
				List<String> worldList = new ArrayList<String>();
				y.load(f);
				for (String k : y.getKeys(false))
					worldList.add(k);
				worlds.put(plugin.getName(), worldList);
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
		for (Minigame mg : Minigame.getMinigameInstances())
			for (Round r : mg.getRoundList()){
				MGPlayer mp = r.getMGPlayer(p);
				if (mp != null){
					try {
						String pUUID = UUIDFetcher.getUUIDOf(p).toString();
						UUIDFetcher.removeUUID(p);
						mp.removeFromRound();
						YamlConfiguration y = new YamlConfiguration();
						File f = new File(Main.plugin.getDataFolder(), "offlineplayers.yml");
						if (!f.exists())
							f.createNewFile();
						y.load(f);
						Location el = mg.getConfigManager().getDefaultExitLocation();
						y.set(pUUID + ".w", el.getWorld().getName());
						y.set(pUUID + ".x", el.getX());
						y.set(pUUID + ".y", el.getY());
						y.set(pUUID + ".z", el.getZ());
						y.save(f);
					}
					catch (Exception ex){
						ex.printStackTrace();
						Main.log.severe("An exception occurred while saving data for " + p);
					}
				}
			}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent e){
		Player pl = null;
		Player p2 = null; // don't ask why it's named like this
		if (e.getEntity() instanceof Player)
			p2 = (Player)e.getEntity();
		if (e instanceof EntityDamageByEntityEvent){
			Entity damager = ((EntityDamageByEntityEvent)e).getDamager();
			if (damager instanceof Player) // damager is a player
				pl = (Player)damager;
			else if (damager instanceof Projectile) // damager is an arrow or something
				if (((Projectile)damager).getShooter() instanceof Player)
					pl = (Player)((Projectile)damager).getShooter(); // a player shot the projectile (e.g. an arrow from a bow)

			//TODO: probably rewrite this bit at some point
			if (pl != null || p2 != null){
				for (Minigame mg : Minigame.getMinigameInstances()){
					if (pl != null){
						MGPlayer p = mg.getMGPlayer(pl.getName());
						if (p != null && (p.isSpectating() || !p.getRound().isPvPAllowed())){
							e.setCancelled(true); // we don't want any spooky ghosts meddling in the affairs of the living
							return;
						}
					}
					if (p2 != null){
						MGPlayer p = mg.getMGPlayer(p2.getName());
						if (p != null && (p.isSpectating() || !p.getRound().isDamageAllowed())){
							e.setCancelled(true); // we don't want any spooky ghosts being harassed by the living
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
				if (p != null && p.getRound() != null && p.getRound().getConfigManager().isOverrideDeathEvent()){
					// override the death event with a custom one
					double actualDamage = 0;
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
					}
					if (actualDamage >= ((Player)e.getEntity()).getHealth()){
						e.setCancelled(true);
						Bukkit.getPluginManager().callEvent(new MGPlayerDeathEvent(p));
					}
					else if (mg.getConfigManager().isForcePreciseDamage()){
						e.setCancelled(true);
						p2.setHealth(p2.getHealth() - actualDamage);
					}
					break;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
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
			if (!f.exists())
				f.createNewFile();
			y.load(f);
			String pUUID = UUIDFetcher.getUUIDOf(p).toString();
			if (y.isSet(pUUID)){
				final String ww = y.getString(pUUID + ".w");
				final double xx = y.getDouble(pUUID + ".x");
				final double yy = y.getDouble(pUUID + ".y");
				final double zz = y.getDouble(pUUID + ".z");
				MGPlayer mp = new MGPlayer("MGLib", p, "null");
				mp.reset(new Location(Bukkit.getWorld(ww), xx, yy, zz));
				mp = null;
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
						try {
							p.removeFromRound(l);
						}
						catch (PlayerNotPresentException ex){} // this can never happen
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
								catch (PlayerNotPresentException ex){} // this can never happen
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
				if (r.isRollbackEnabled())
					if (r.getPlayers().containsKey(e.getPlayer().getName())){
						if (!mg.getConfigManager().isBlockPlaceAllowed())
							e.setCancelled(true);
						mg.getRollbackManager().logBlockChange(e.getBlock(),
								e.getBlockReplacedState().getType().toString(), r.getArena());
					}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e){
		for (Minigame mg : Minigame.getMinigameInstances())
			for (Round r : mg.getRoundList())
				if (r.isRollbackEnabled())
					if (r.getPlayers().containsKey(e.getPlayer().getName())){
						if (!mg.getConfigManager().isBlockBreakAllowed())
							e.setCancelled(true);
						mg.getRollbackManager().logBlockChange(e.getBlock(),
								e.getBlock().getType().toString(), r.getArena());
					}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent e){
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockBurnAllowed()){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockFade(BlockFadeEvent e){
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockFadeAllowed()){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockIgnite(BlockIgniteEvent e){
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockIgniteAllowed()){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPhysics(BlockPhysicsEvent e){
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockPhysicsAllowed()){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPiston(BlockPistonExtendEvent e){
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockPistonAllowed()){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPiston(BlockPistonRetractEvent e){
		String w = e.getBlock().getWorld().getName();
		for (String p : worlds.keySet()){
			for (int i = 0; i < worlds.get(p).size(); i++){
				if (worlds.get(p).get(i).equals(w)){
					if (!Minigame.getMinigameInstance(p).getConfigManager().isBlockPistonAllowed()){
						e.setCancelled(true);
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
						if (!e.getLine(1).equalsIgnoreCase("players") ||
								MGUtil.isInteger(e.getLine(3))){ // make sure last line (sign index) is a number if it's a player sign
							try {
								int index = MGUtil.isInteger(e.getLine(3)) ? Integer.parseInt(e.getLine(3)) : 0;
								mg.getLobbyManager().add(e.getBlock().getLocation(), e.getLine(2), LobbyType.fromString(e.getLine(1)), index);
							}
							catch (ArenaNotExistsException ex){
								e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("arena-not-exists"));
							}
							catch (IllegalArgumentException ex){
								if (ex.getMessage().contains("index"))
									e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("invalid-sign-index"));
								else if (ex.getMessage().contains("Invalid string!"))
									e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("invalid-sign-type"));
								else
									ex.printStackTrace();
							}
						}
						else
							e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("invalid-sign-index"));
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
			if (p != null)
				if (p.isSpectating()){
					e.setCancelled(true);
					return;
				}
		}
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
						Round r = mg.getRound(ls.getArena());
						if (r == null){
							try {
								r = mg.createRound(ls.getArena());
							}
							catch (ArenaNotExistsException ex){
								e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("arena-load-fail").replace("%", ls.getArena()));
							}
						}
						try {
							r.addPlayer(e.getPlayer().getName());
							Bukkit.getPluginManager().callEvent(new LobbyClickEvent(r.getMGPlayer(e.getPlayer().getName()), r, ls));
						}
						catch (PlayerOfflineException ex){} // this can never happen
						catch (PlayerPresentException e1){
							e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("in-round"));
						}
						catch (RoundFullException e1){
							e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("round-full"));
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onMGRoundEvent(MGRoundEvent e){
		e.getRound().getMinigame().getLobbyManager().update(e.getRound().getArena());
	}

	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e){
		if (e.getMessage().startsWith("kit")){
			for (Minigame mg : Minigame.getMinigameInstances())
				if (mg.isPlayer(e.getPlayer().getName())){
					if (!mg.getConfigManager().areKitsAllowed()){
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("no-kits"));
					}
				}
		}
		else if (e.getMessage().startsWith("msg") || e.getMessage().startsWith("tell") || e.getMessage().startsWith("r ") ||
				e.getMessage().startsWith("me")){
			for (Minigame mg : Minigame.getMinigameInstances())
				if (mg.isPlayer(e.getPlayer().getName())){
					if (!mg.getConfigManager().arePMsAllowed()){
						e.setCancelled(true);
						e.getPlayer().sendMessage(ChatColor.RED + locale.getMessage("no-pms"));
					}
				}
		}
	}

}
