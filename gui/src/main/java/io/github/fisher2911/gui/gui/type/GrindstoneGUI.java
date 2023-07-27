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
 * A {@link GUI} with an {@link GUI.Type#GRINDSTONE} {@link GUI.Type}.
 */
@SuppressWarnings("unused")
public class GrindstoneGUI extends GUI {

    private GrindstoneGUI(
            String title,
            List<Map<GUISlot, GUIItem>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            Metadata metadata,
            List<Pattern> patterns,
            boolean expandable
    ) {
        super(title, guiItems, listeners, Type.GRINDSTONE, metadata, patterns, expandable);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.Grindstone#FIRST} slot.
     */
    public @Nullable GUIItem getFirstItem() {
        return this.getItem(GUISlot.Grindstone.FIRST);
    }

    /**
     * @param item The {@link GUIItem} to set in the {@link GUISlot.Grindstone#FIRST} slot.
     */
    public void setFirstItem(GUIItem item) {
        this.setItem(GUISlot.Grindstone.FIRST, item);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.Grindstone#SECOND} slot.
     */
    public @Nullable GUIItem getSecondItem() {
        return this.getItem(GUISlot.Grindstone.SECOND);
    }

    /**
     * @param item The {@link GUIItem} to set in the {@link GUISlot.Grindstone#SECOND} slot.
     */
    public void setSecondItem(GUIItem item) {
        this.setItem(GUISlot.Grindstone.SECOND, item);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.Grindstone#RESULT} slot.
     */
    public @Nullable GUIItem getResultItem() {
        return this.getItem(GUISlot.Grindstone.RESULT);
    }

    /**
     * @param item The {@link GUIItem} to set in the {@link GUISlot.Grindstone#RESULT} slot.
     */
    public void setResultItem(GUIItem item) {
        this.setItem(GUISlot.Grindstone.RESULT, item);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.GRINDSTONE, title);
        return this.inventory;
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.Grindstone.FIRST;
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.Grindstone.SECOND;
    }

    /**
     * @return A new {@link Builder} for {@link GrindstoneGUI}s.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends GUI.Builder<Builder, GrindstoneGUI> {

        protected Builder() {
        }

        /**
         *
         * @param item The {@link GUIItem} to set in the {@link GUISlot.Grindstone#FIRST} slot.
         * @return This {@link Builder}.
         */
        public Builder firstItem(GUIItem item) {
            this.currentGuiItems.put(GUISlot.Grindstone.FIRST, item);
            return this;
        }

        /**
         *
         * @param item The {@link GUIItem} to set in the {@link GUISlot.Grindstone#SECOND} slot.
         * @return This {@link Builder}.
         */
        public Builder secondItem(GUIItem item) {
            this.currentGuiItems.put(GUISlot.Grindstone.SECOND, item);
            return this;
        }

        /**
         *
         * @param item The {@link GUIItem} to set in the {@link GUISlot.Grindstone#RESULT} slot.
         * @return This {@link Builder}.
         */
        public Builder resultItem(GUIItem item) {
            this.currentGuiItems.put(GUISlot.Grindstone.RESULT, item);
            return this;
        }

        /**
         *
         * @return A new {@link GrindstoneGUI} with the properties of this {@link Builder}.
         */
        public GrindstoneGUI build() {
            return new GrindstoneGUI(this.title, this.guiItems, this.listeners, this.metadata, this.patterns, this.expandable);
        }

    }

}
