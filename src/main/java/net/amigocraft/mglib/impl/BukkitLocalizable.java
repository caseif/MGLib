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
import net.amigocraft.mglib.MGUtil;
import net.amigocraft.mglib.UUIDFetcher;
import net.amigocraft.mglib.api.Color;
import net.amigocraft.mglib.api.Localizable;
import net.amigocraft.mglib.api.MGPlayer;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

public class BukkitLocalizable implements Localizable {

	private static final String FALLBACK_LOCALE = "enUS";
	private static final String DEFAULT_LOCALE;

	private String key;
	private Map<String, String> locales = Maps.newHashMap();

	static {
		FileConfiguration config = MGUtil.getPlugin().getConfig();
		if (config.contains("locale")) {
			DEFAULT_LOCALE = config.getString("locale");
		}
		else {
			DEFAULT_LOCALE = FALLBACK_LOCALE;
		}
	}

	BukkitLocalizable(String key) {
		this.key = key;
	}

	void addLocale(String locale, String message) {
		locales.put(locale, message);
	}

	@Override
	public String getKey() {
		return this.key;
	}

	@Override
	public String localize(String... replacements) {
		String message = this.localizeIn(DEFAULT_LOCALE, replacements);
		if (message.equals(key)) {
			return this.localizeIn(FALLBACK_LOCALE, replacements);
		}
		else {
			return message;
		}
	}

	@Override
	public String localizeIn(String locale, String... replacements) {
		return locales.containsKey(locale) ? locales.get(locale) : this.getKey();
	}

	@Override
	public String localizeFor(String playerName, String... replacements) throws IllegalArgumentException {
		try {
			return this.localizeFor(UUIDFetcher.getUUIDOf(playerName));
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
		throw new NotImplementedException();
	}

	@Override
	public String localizeFor(MGPlayer player, String... replacements) throws IllegalArgumentException {
		return this.localizeFor(player.getName());
	}

	@Override
	public void sendTo(String playerName, String... replacements) throws IllegalArgumentException {
		try {
			this.sendTo(UUIDFetcher.getUUIDOf(playerName));
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
			this.sendTo(UUIDFetcher.getUUIDOf(playerName), color);
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
		this.sendTo(playerUuid, Color.WHITE);
	}

	@Override
	public void sendTo(UUID playerUuid, Color color, String... replacements) throws IllegalArgumentException {
		Player player = Bukkit.getPlayer(playerUuid);
		if (player != null) {
			player.sendMessage(this.localizeFor(playerUuid));
		}
		else {
			throw new IllegalArgumentException();
		}
	}
}
