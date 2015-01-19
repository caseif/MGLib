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

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("deprecation")
class InventorySerializer {
	public static String InventoryToString(Inventory invInventory) {
		String serialization = invInventory.getSize() + ";" + invInventory.getType().toString() + ";";
		for (int i = 0; i < invInventory.getSize(); i++) {
			ItemStack is = invInventory.getItem(i);
			if (is != null) {
				String serializedItemStack = new String();

				String isType = String.valueOf(is.getType().getId());
				serializedItemStack += "t@" + isType;

				if (is.getDurability() != 0) {
					String isDurability = String.valueOf(is.getDurability());
					serializedItemStack += ":d@" + isDurability;
				}

				if (is.getAmount() != 1) {
					String isAmount = String.valueOf(is.getAmount());
					serializedItemStack += ":a@" + isAmount;
				}

				Map<Enchantment, Integer> isEnch = is.getEnchantments();
				if (isEnch.size() > 0) {
					for (Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
						serializedItemStack += ":e@" + ench.getKey().getId() + "@" + ench.getValue();
					}
				}

				serialization += i + "#" + serializedItemStack + ";";
			}
		}
		return serialization;
	}

	public static Inventory StringToInventory(String invString) {
		String[] serializedBlocks = invString.split(";");
		String size = serializedBlocks[0];
		String type = serializedBlocks[1];
		Inventory deserializedInventory = type.equals("CHEST") ?
				Bukkit.getServer().createInventory(null, Integer.parseInt(size)) :
				Bukkit.getServer().createInventory(null, InventoryType.valueOf(type));

		for (int i = 2; i < serializedBlocks.length; i++) {
			String[] serializedBlock = serializedBlocks[i].split("#");
			int stackPosition = Integer.valueOf(serializedBlock[0]);

			if (stackPosition >= deserializedInventory.getSize()) {
				continue;
			}

			ItemStack is = null;
			Boolean createdItemStack = false;

			String[] serializedItemStack = serializedBlock[1].split(":");
			for (String itemInfo : serializedItemStack) {
				String[] itemAttribute = itemInfo.split("@");
				if (itemAttribute[0].equals("t")) {
					is = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
					createdItemStack = true;
				}
				else if (itemAttribute[0].equals("d") && createdItemStack) {
					is.setDurability(Short.valueOf(itemAttribute[1]));
				}
				else if (itemAttribute[0].equals("a") && createdItemStack) {
					is.setAmount(Integer.valueOf(itemAttribute[1]));
				}
				else if (itemAttribute[0].equals("e") && createdItemStack) {
					is.addEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
				}
			}
			deserializedInventory.setItem(stackPosition, is);
		}

		return deserializedInventory;
	}
}