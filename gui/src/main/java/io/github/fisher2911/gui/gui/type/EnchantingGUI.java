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

package io.github.fisher2911.gui.gui.type;

import io.github.fisher2911.common.metadata.Metadata;
import io.github.fisher2911.gui.event.GUIEvent;
import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.gui.gui.GUISlot;
import io.github.fisher2911.gui.gui.pattern.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A {@link GUI} with an {@link GUI.Type#ENCHANTING} {@link GUI.Type}.
 */
@SuppressWarnings("unused")
public class EnchantingGUI extends GUI {

    private EnchantingGUI(
            String title,
            List<Map<GUISlot, GUIItem>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            Metadata metadata,
            List<Pattern> patterns,
            boolean expandable
    ) {
        super(title, guiItems, listeners, Type.ENCHANTING, metadata, patterns, expandable);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.EnchantingTable#ITEM} slot.
     */
    public @Nullable GUIItem getItem() {
        return this.getItem(GUISlot.EnchantingTable.ITEM);
    }

    /**
     * @param item The {@link GUIItem} to set in the {@link GUISlot.EnchantingTable#ITEM} slot.
     */
    public void setItem(GUIItem item) {
        this.setItem(GUISlot.EnchantingTable.ITEM, item);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.EnchantingTable#LAPIS} slot.
     */
    public @Nullable GUIItem getLapisItem() {
        return this.getItem(GUISlot.EnchantingTable.LAPIS);
    }

    /**
     * @param item The {@link GUIItem} to set in the {@link GUISlot.EnchantingTable#LAPIS} slot.
     */
    public void setLapisItem(GUIItem item) {
        this.setItem(GUISlot.EnchantingTable.LAPIS, item);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.ENCHANTING, title);
        return this.inventory;
    }

    /**
     * @return A new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.EnchantingTable.ITEM;
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.EnchantingTable.LAPIS;
    }

    public static class Builder extends GUI.Builder<Builder, EnchantingGUI> {

        protected Builder() {
        }

        /**
         * @param item The {@link GUIItem} to set in the {@link GUISlot.EnchantingTable#ITEM} slot.
         * @return This {@link Builder} instance.
         */
        public Builder item(GUIItem item) {
            return this.set(GUISlot.EnchantingTable.ITEM, item);
        }

        /**
         * @param item The {@link GUIItem} to set in the {@link GUISlot.EnchantingTable#LAPIS} slot.
         * @return This {@link Builder} instance.
         */
        public Builder lapis(GUIItem item) {
            return this.set(GUISlot.EnchantingTable.LAPIS, item);
        }

        /**
         * @return A new {@link EnchantingGUI} instance.
         */
        public EnchantingGUI build() {
            return new EnchantingGUI(this.title, this.guiItems, this.listeners, this.metadata, this.patterns, this.expandable);
        }

    }

}
