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

package io.github.fisher2911.fisherlib.util.builder;

import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.placeholder.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class BaseItemBuilder {

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

    protected BaseItemBuilder(Material material) {
        this.material = material;
        this.itemMeta = Bukkit.getItemFactory().getItemMeta(material);
        this.amount = 1;
    }

    protected BaseItemBuilder(ItemStack itemStack) {
        this.material = itemStack.getType();
        this.itemMeta = itemStack.getItemMeta();
        this.amount = itemStack.getAmount();
    }

    public static <T extends BaseItemBuilder> T from(Material material) {
        if (material == Material.PLAYER_HEAD) return (T) new SkullBuilder(material);
        return (T) new BaseItemBuilder(material);
    }

    public static <T extends BaseItemBuilder> T from(ItemStack itemStack) {
        if (itemStack.getType() == Material.PLAYER_HEAD) return (T) new SkullBuilder(itemStack);
        return (T) new BaseItemBuilder(itemStack);
    }

    public <T extends BaseItemBuilder> T amount(int amount) {
        this.amount = amount;
        return (T) this;
    }

    public <T extends BaseItemBuilder> T name(String name) {
        if (this.itemMeta == null) return (T) this;
        this.itemMeta.setDisplayName(name);
        return (T) this;
    }

    public <T extends BaseItemBuilder> T lore(List<String> lore) {
        if (this.itemMeta == null) return (T) this;
        this.itemMeta.setLore(lore);
        return (T) this;
    }

    public <T extends BaseItemBuilder> T unbreakable() {
        if (this.itemMeta == null) return (T) this;
        this.itemMeta.setUnbreakable(true);
        return (T) this;
    }

    public <T extends BaseItemBuilder> T enchant(Enchantment enchantment, int level) {
        if (this.itemMeta == null) return (T) this;
        this.itemMeta.addEnchant(enchantment, level, true);
        return (T) this;
    }

    public <T extends BaseItemBuilder> T flag(ItemFlag... flags) {
        if (this.itemMeta == null) return (T) this;
        this.itemMeta.addItemFlags(flags);
        return (T) this;
    }

    public <T extends BaseItemBuilder> T enchantments(Map<Enchantment, Integer> enchants) {
        if (this.itemMeta == null) return (T) this;
        for (final Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            this.enchant(entry.getKey(), entry.getValue());
        }
        return (T) this;
    }

    public <T extends BaseItemBuilder> T pdc(Consumer<PersistentDataContainer> consumer) {
        if (this.itemMeta == null) return (T) this;
        consumer.accept(this.itemMeta.getPersistentDataContainer());
        return (T) this;
    }

    public <T extends BaseItemBuilder> T glow(boolean glow) {
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

    public ItemStack build(Placeholders placeholders, Object... parsePlaceholders) {
        final ItemStack itemStack = new ItemStack(this.material);
        itemStack.setAmount(Math.max(1, this.amount));
        if (this.itemMeta == null) return itemStack;
        final ItemMeta itemMeta = this.itemMeta.clone();
        final String name = itemMeta.getDisplayName();
        itemMeta.setDisplayName(MessageHandler.serialize(placeholders.apply(name, parsePlaceholders)));
        final List<String> lore = itemMeta.getLore();
        if (lore != null) {
            itemMeta.setLore(this.buildLore(lore, placeholders, parsePlaceholders));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack build() {
        final ItemStack itemStack = new ItemStack(this.material);
        itemStack.setAmount(Math.max(1, this.amount));
        if (this.itemMeta == null) return itemStack;
        final ItemMeta itemMeta = this.itemMeta.clone();
        final String name = itemMeta.getDisplayName();
        itemMeta.setDisplayName(MessageHandler.serialize(name));
        final List<String> lore = itemMeta.getLore();
        if (lore != null) {
            itemMeta.setLore(this.buildLore(lore));
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private List<String> buildLore(List<String> lore, Placeholders placeholders, Object... parsePlaceholders) {
        return lore.stream().
                map(s -> MessageHandler.serialize(placeholders.apply(s, parsePlaceholders))).
                collect(Collectors.toList());
    }

    private List<String> buildLore(List<String> lore) {
        return lore.stream().
                map(MessageHandler::serialize).
                collect(Collectors.toList());
    }

    public <T extends BaseItemBuilder> T copy() {
        final BaseItemBuilder builder = new BaseItemBuilder(this.material).amount(this.amount);
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
