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
 * A {@link GUI} with an {@link GUI.Type#CARTOGRAPHY} {@link GUI.Type}.
 */
@SuppressWarnings("unused")
public class CartographyGUI extends GUI {

    private CartographyGUI(
            String title,
            Map<GUISlot, GUIItem> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            Metadata metadata,
            List<Pattern> patterns
    ) {
        super(title, guiItems, listeners, Type.CARTOGRAPHY, metadata, patterns);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.CartographyTable#MAP} slot.
     */
    public @Nullable GUIItem getMapItem() {
        return this.getItem(GUISlot.CartographyTable.MAP);
    }

    /**
     *
     * @param item The {@link GUIItem} to set in the {@link GUISlot.CartographyTable#MAP} slot.
     */
    public void setMapItem(GUIItem item) {
        this.setItem(GUISlot.CartographyTable.MAP, item);
    }

    /**
     *
     * @return The {@link GUIItem} in the {@link GUISlot.CartographyTable#PAPER} slot.
     */
    public @Nullable GUIItem getPaperItem() {
        return this.getItem(GUISlot.CartographyTable.PAPER);
    }

    /**
     *
     * @param item The {@link GUIItem} to set in the {@link GUISlot.CartographyTable#PAPER} slot.
     */
    public void setPaperItem(GUIItem item) {
        this.setItem(GUISlot.CartographyTable.PAPER, item);
    }

    /**
     *
     * @return The {@link GUIItem} in the {@link GUISlot.CartographyTable#OUTPUT} slot.
     */
    public @Nullable GUIItem getOutputItem() {
        return this.getItem(GUISlot.CartographyTable.OUTPUT);
    }

    /**
     *
     * @param item The {@link GUIItem} to set in the {@link GUISlot.CartographyTable#OUTPUT} slot.
     */
    public void setOutputItem(GUIItem item) {
        this.setItem(GUISlot.CartographyTable.OUTPUT, item);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.CARTOGRAPHY, title);
        return this.inventory;
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.CartographyTable.MAP;
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.CartographyTable.PAPER;
    }

    /**
     *
     * @return A new {@link Builder} for {@link CartographyGUI}s.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends GUI.Builder<Builder, CartographyGUI> {

        protected Builder() {
        }

        /**
         *
         * @param item The {@link GUIItem} to set in the {@link GUISlot.CartographyTable#MAP} slot.
         * @return The {@link Builder} instance.
         */
        public Builder mapItem(GUIItem item) {
            this.guiItems.put(GUISlot.CartographyTable.MAP, item);
            return this;
        }

        /**
         *
         * @param item The {@link GUIItem} to set in the {@link GUISlot.CartographyTable#PAPER} slot.
         * @return The {@link Builder} instance.
         */
        public Builder paperItem(GUIItem item) {
            this.guiItems.put(GUISlot.CartographyTable.PAPER, item);
            return this;
        }

        /**
         *
         * @param item The {@link GUIItem} to set in the {@link GUISlot.CartographyTable#OUTPUT} slot.
         * @return The {@link Builder} instance.
         */
        public Builder outputItem(GUIItem item) {
            this.guiItems.put(GUISlot.CartographyTable.OUTPUT, item);
            return this;
        }

        /**
         *
         * @return The built {@link CartographyGUI}.
         */
        public CartographyGUI build() {
            return new CartographyGUI(this.title, this.guiItems, this.listeners, this.metadata, this.patterns);
        }

    }

}
