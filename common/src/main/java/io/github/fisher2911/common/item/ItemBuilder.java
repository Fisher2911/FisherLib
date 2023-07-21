/*
 *     FisherLib
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.fisher2911.common.item;

import io.github.fisher2911.common.placeholder.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemBuilder {

    public static boolean isGlowing(ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return false;
        }
        return itemMeta.hasEnchant(Enchantment.LUCK);
    }

    protected Material material;
    protected int amount;
    protected ItemMeta itemMeta;

    protected ItemBuilder(Material material) {
        this.material = material;
        this.itemMeta = Bukkit.getItemFactory().getItemMeta(material);
        this.amount = 1;
    }

    protected ItemBuilder(ItemStack itemStack) {
        this.material = itemStack.getType();
        this.itemMeta = itemStack.getItemMeta();
        this.amount = itemStack.getAmount();
    }

    public static <T extends ItemBuilder> T from(Material material) {
        if (material == Material.PLAYER_HEAD) return (T) new SkullItemBuilder(material);
        return (T) new ItemBuilder(material);
    }

    public static <T extends ItemBuilder> T from(ItemStack itemStack) {
        if (itemStack.getType() == Material.PLAYER_HEAD) return (T) new SkullItemBuilder(itemStack);
        return (T) new ItemBuilder(itemStack);
    }

    public <T extends ItemBuilder> T amount(int amount) {
        this.amount = amount;
        return (T) this;
    }

    public <T extends ItemBuilder> T name(String name) {
        if (this.itemMeta == null) return (T) this;
        this.itemMeta.setDisplayName(name);
        return (T) this;
    }

    public <T extends ItemBuilder> T lore(List<String> lore) {
        if (this.itemMeta == null) return (T) this;
        this.itemMeta.setLore(lore);
        return (T) this;
    }

    public <T extends ItemBuilder> T lore(String... lore) {
        if (this.itemMeta == null) return (T) this;
        this.itemMeta.setLore(Arrays.asList(lore));
        return (T) this;
    }

    public <T extends ItemBuilder> T unbreakable() {
        if (this.itemMeta == null) return (T) this;
        this.itemMeta.setUnbreakable(true);
        return (T) this;
    }

    public <T extends ItemBuilder> T enchant(Enchantment enchantment, int level) {
        if (this.itemMeta == null) return (T) this;
        this.itemMeta.addEnchant(enchantment, level, true);
        return (T) this;
    }

    public <T extends ItemBuilder> T flag(ItemFlag... flags) {
        if (this.itemMeta == null) return (T) this;
        this.itemMeta.addItemFlags(flags);
        return (T) this;
    }

    public <T extends ItemBuilder> T enchantments(Map<Enchantment, Integer> enchants) {
        if (this.itemMeta == null) return (T) this;
        for (final Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            this.enchant(entry.getKey(), entry.getValue());
        }
        return (T) this;
    }

    public <T extends ItemBuilder> T pdc(Consumer<PersistentDataContainer> consumer) {
        if (this.itemMeta == null) return (T) this;
        consumer.accept(this.itemMeta.getPersistentDataContainer());
        return (T) this;
    }

    public <T extends ItemBuilder> T glow(boolean glow) {
        if (this.itemMeta == null) return (T) this;
        if (glow) {
            final boolean empty = this.itemMeta.getEnchants().isEmpty();
            this.enchant(Enchantment.LUCK, 1);
            if (!empty) return (T) this;
            this.flag(ItemFlag.HIDE_ENCHANTS);
            return (T) this;
        }
        this.itemMeta.removeEnchant(Enchantment.LUCK);
        if (!this.itemMeta.getEnchants().isEmpty()) return (T) this;
        this.itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        return (T) this;
    }

    public ItemStack build() {
        final ItemStack itemStack = new ItemStack(this.material);
        itemStack.setAmount(Math.max(1, this.amount));
        if (this.itemMeta == null) return new ItemStack(itemStack);
        final ItemMeta itemMeta = this.itemMeta.clone();
        itemStack.setItemMeta(itemMeta);
        return new ItemStack(itemStack);
    }

    public ItemStack build(Placeholders placeholders, Object... parsePlaceholders) {
        final ItemStack itemStack = this.build();
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return itemStack;
        }
        itemMeta.setDisplayName(placeholders.apply(itemMeta.getDisplayName(), parsePlaceholders));
        final List<String> lore = itemMeta.getLore();
        if (lore == null) {
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        itemMeta.setLore(lore.stream()
                .map(s -> placeholders.apply(s, parsePlaceholders))
                .collect(Collectors.toList()));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public <T extends ItemBuilder> T copy() {
        final ItemBuilder builder = new ItemBuilder(this.material).amount(this.amount);
        if (this.itemMeta == null) return (T) builder;
        builder.itemMeta = this.itemMeta.clone();
        return (T) builder;
    }

    @Override
    public String toString() {
        return "ItemBuilder{" +
                "material=" + material +
                ", amount=" + amount +
                ", itemMeta=" + itemMeta +
                '}';
    }

}
