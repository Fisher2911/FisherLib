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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class SkullItemBuilder extends ItemBuilder {

    private String ownerUUIDPlaceholder;

    public SkullItemBuilder(Material material) {
        super(material);
    }

    public SkullItemBuilder(ItemStack itemStack) {
        super(itemStack);
    }

    public SkullItemBuilder owner(String ownerUUIDPlaceholder) {
        this.ownerUUIDPlaceholder = ownerUUIDPlaceholder;
        return this;
    }

    public ItemStack build(Placeholders placeholders, Object... parsePlaceholders) {
        final ItemStack itemStack = super.build(placeholders, parsePlaceholders);
        if (this.ownerUUIDPlaceholder == null) return itemStack;
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof final SkullMeta meta)) return itemStack;
        final String ownerUUID = placeholders.apply(this.ownerUUIDPlaceholder, parsePlaceholders);
        if (ownerUUID == null) return itemStack;
        try {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(ownerUUID)));
            itemStack.setItemMeta(meta);
            return itemStack;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Could not parse UUID: " + ownerUUID);
        }
    }

    public ItemStack build() {
        final ItemStack itemStack = super.build();
        if (this.ownerUUIDPlaceholder == null) return itemStack;
        final ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof final SkullMeta meta)) return itemStack;
        try {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(this.ownerUUIDPlaceholder)));
            itemStack.setItemMeta(meta);
            return itemStack;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Could not parse UUID: " + this.ownerUUIDPlaceholder);
        }
    }

    public <T extends ItemBuilder> T copy() {
        final SkullItemBuilder builder = new SkullItemBuilder(this.material).amount(this.amount);
        if (this.itemMeta == null) return (T) builder;
        builder.itemMeta = this.itemMeta.clone();
        return (T) builder;
    }

    public String getOwnerUUIDPlaceholder() {
        return ownerUUIDPlaceholder;
    }


}
