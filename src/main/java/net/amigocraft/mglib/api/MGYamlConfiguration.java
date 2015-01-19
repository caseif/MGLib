/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Maxim Roncacé
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
package net.amigocraft.mglib.api;

import org.bukkit.Color;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom class for managing YAML files which forces the first section title to be made lowercase. This is to allow
 * for case-insensitive arena names.
 * @since 0.3.0
 */
public class MGYamlConfiguration extends YamlConfiguration {

	@Override
	public Object get(String path){
		return super.get(getNewKey(path));
	}

	@Override
	public Object get(String path, Object def){
		return super.get(getNewKey(path), def);
	}

	@Override
	public boolean getBoolean(String path){
		return super.getBoolean(getNewKey(path));
	}

	@Override
	public boolean getBoolean(String path, boolean def){
		return super.getBoolean(getNewKey(path), def);
	}

	@Override
	public List<Boolean> getBooleanList(String path){
		return super.getBooleanList(getNewKey(path));
	}

	@Override
	public List<Byte> getByteList(String path){
		return super.getByteList(getNewKey(path));
	}

	@Override
	public List<Character> getCharacterList(String path){
		return super.getCharacterList(getNewKey(path));
	}

	@Override
	public Color getColor(String path){
		return super.getColor(getNewKey(path));
	}

	@Override
	public Color getColor(String path, Color def){
		return super.getColor(getNewKey(path), def);
	}

	@Override
	public ConfigurationSection getConfigurationSection(String path){
		return super.getConfigurationSection(getNewKey(path));
	}

	@Override
	public Object getDefault(String path){
		return super.getDefault(getNewKey(path));
	}

	@Override
	public double getDouble(String path){
		return super.getDouble(getNewKey(path));
	}

	@Override
	public double getDouble(String path, double def){
		return super.getDouble(getNewKey(path), def);
	}

	@Override
	public List<Double> getDoubleList(String path){
		return super.getDoubleList(getNewKey(path));
	}

	@Override
	public List<Float> getFloatList(String path){
		return super.getFloatList(getNewKey(path));
	}

	@Override
	public int getInt(String path){
		return super.getInt(getNewKey(path));
	}

	@Override
	public int getInt(String path, int def){
		return super.getInt(getNewKey(path), def);
	}

	@Override
	public List<Integer> getIntegerList(String path){
		return super.getIntegerList(getNewKey(path));
	}

	@Override
	public ItemStack getItemStack(String path){
		return super.getItemStack(getNewKey(path));
	}

	@Override
	public ItemStack getItemStack(String path, ItemStack def){
		return super.getItemStack(getNewKey(path), def);
	}

	@Override
	public List<?> getList(String path){
		return super.getList(getNewKey(path));
	}

	@Override
	public List<?> getList(String path, List<?> def){
		return super.getList(getNewKey(path), def);
	}

	@Override
	public long getLong(String path){
		return super.getLong(getNewKey(path));
	}

	@Override
	public long getLong(String path, long def){
		return super.getLong(getNewKey(path), def);
	}

	@Override
	public List<Long> getLongList(String path){
		return super.getLongList(getNewKey(path));
	}

	@Override
	public List<Map<?, ?>> getMapList(String path){
		return super.getMapList(getNewKey(path));
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String path){
		return super.getOfflinePlayer(getNewKey(path));
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def){
		return super.getOfflinePlayer(getNewKey(path), def);
	}

	@Override
	public List<Short> getShortList(String path){
		return super.getShortList(getNewKey(path));
	}

	@Override
	public String getString(String path){
		return super.getString(getNewKey(path));
	}

	@Override
	public String getString(String path, String def){
		return super.getString(getNewKey(path), def);
	}

	@Override
	public List<String> getStringList(String path){
		return super.getStringList(getNewKey(path));
	}

	@Override
	public Vector getVector(String path){
		return super.getVector(getNewKey(path));
	}

	@Override
	public Vector getVector(String path, Vector def){
		return super.getVector(getNewKey(path), def);
	}

	@Override
	public void set(String path, Object value){
		super.set(getNewKey(path), value);
	}

	@Override
	public boolean contains(String path){
		return super.contains(getNewKey(path));
	}

	@Override
	public void addDefault(String path, Object value){
		super.addDefault(path, value);
	}

	@Override
	public void addDefaults(Map<String, Object> defaults){
		Map<String, Object> newMap = new HashMap<String, Object>();
		for (String s : defaults.keySet()){
			newMap.put(getNewKey(s), defaults.get(s));
		}
		super.addDefaults(newMap);
	}

	private static String getNewKey(String path){
		if (path != null){
			if (path.contains("\\.")){
				String[] pathArray = path.split("\\.");
				pathArray[0] = pathArray[0].toLowerCase();
				String newPath = "";
				for (String s : pathArray){
					newPath += s;
				}
				return newPath;
			}
			return path.toLowerCase();
		}
		return null;
	}

}
