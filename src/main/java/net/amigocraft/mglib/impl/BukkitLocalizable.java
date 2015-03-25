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
package net.amigocraft.mglib.impl;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import net.amigocraft.mglib.Main;
import net.amigocraft.mglib.UUIDFetcher;
import net.amigocraft.mglib.api.Color;
import net.amigocraft.mglib.api.Locale;
import net.amigocraft.mglib.api.Localizable;
import net.amigocraft.mglib.api.MGPlayer;
import net.amigocraft.mglib.util.NmsUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

public class BukkitLocalizable implements Localizable {

	private static final String FALLBACK_LOCALE = "enUS";

	private Locale parent;
	private String key;
	private Map<String, String> locales = Maps.newHashMap();

	BukkitLocalizable(Locale parent, String key) {
		this.parent = parent;
		this.key = key;
	}

	void addLocale(String locale, String message) {
		locale = locale.replace("_", "").replace("-", ""); // normalize locale code
		locales.put(locale, message);
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public Locale getParent() {
		return this.parent;
	}

	@Override
	public String localize(String... replacements) {
		return this.localizeIn(Main.getServerLocale(), replacements);
	}

	@Override
	public String localizeIn(String locale, String... replacements) {
		locale = locale.replace("_", "").replace("-", "").toLowerCase(); // normalize locale code
		if (locales.containsKey(locale)) {
			String message = locales.get(locale);
			for (int i = 0; i < replacements.length; i++) {
				message = message.replaceAll("%" + (i + 1), replacements[i]);
			}
			return message;
		}
		else if (!locale.equals(FALLBACK_LOCALE)) {
			if (!locale.equals(Main.getServerLocale())) {
				return this.localizeIn(Main.getServerLocale(), replacements);
			}
			else {
				return this.localizeIn(FALLBACK_LOCALE, replacements);
			}
		}
		else {
			return this.getKey();
		}
	}

	@Override
	public String localizeFor(String playerName, String... replacements) throws IllegalArgumentException {
		try {
			return this.localizeFor(UUIDFetcher.getUUIDOf(playerName), replacements);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		catch (ParseException ex) {
			ex.printStackTrace();
		}
		return this.getKey();
	}

	@Override
	public String localizeFor(UUID playerUuid, String... replacements) throws IllegalArgumentException {
		Player player = Bukkit.getPlayer(playerUuid);
		String locale = null;
		if (player != null) {
			locale = NmsUtil.getLocale(player);
		}
		return this.localizeIn(locale != null ? locale : Main.getServerLocale(), replacements);
	}

	@Override
	public String localizeFor(MGPlayer player, String... replacements) throws IllegalArgumentException {
		return this.localizeFor(player.getName(), replacements);
	}

	@Override
	public void sendTo(String playerName, String... replacements) throws IllegalArgumentException {
		try {
			this.sendTo(UUIDFetcher.getUUIDOf(playerName), replacements);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		catch (ParseException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void sendTo(String playerName, Color color, String... replacements) throws IllegalArgumentException {
		try {
			this.sendTo(UUIDFetcher.getUUIDOf(playerName), color, replacements);
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		catch (ParseException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void sendTo(UUID playerUuid, String... replacements) throws IllegalArgumentException {
		this.sendTo(playerUuid, Color.RESET, replacements);
	}

	@Override
	public void sendTo(UUID playerUuid, Color color, String... replacements) throws IllegalArgumentException {
		Player player = Bukkit.getPlayer(playerUuid);
		if (player != null) {
			player.sendMessage(ChatColor.valueOf(color.name()) + this.localizeFor(playerUuid, replacements));
		}
		else {
			throw new IllegalArgumentException();
		}
	}
}
