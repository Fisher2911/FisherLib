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
 * A {@link GUI} with an {@link GUI.Type#BEACON} {@link GUI.Type}.
 */
public class BeaconGUI extends GUI {

    private BeaconGUI(
            String title,
            Map<GUISlot, GUIItem> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            Metadata metadata,
            List<Pattern> patterns
    ) {
        super(title, guiItems, listeners, Type.BEACON, metadata, patterns);
    }

    /**
     *
     * @return the {@link GUIItem} in the {@link GUISlot.Number#ZERO} slot.
     */
    public @Nullable GUIItem getItem() {
        return this.getItem(GUISlot.of(0));
    }

    /**
     *
     * @param item the {@link GUIItem} to set in the {@link GUISlot.Number#ZERO} slot.
     */
    public void setItem(@Nullable GUIItem item) {
        this.setItem(GUISlot.of(0), item);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.BEACON, title);
        return this.inventory;
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.Number.ZERO;
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.Number.ZERO;
    }

    /**
     *
     * @return a new {@link Builder} instance.
     */
    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder extends GUI.Builder<Builder, BeaconGUI> {

        protected Builder() {
        }

        /**
         *
         * @param item the {@link GUIItem} to set in the {@link GUISlot.Number#ZERO} slot.
         * @return this {@link Builder} instance.
         */
        public Builder item(@Nullable GUIItem item) {
            this.set(GUISlot.of(0), item);
            return this;
        }

        public BeaconGUI build() {
            return new BeaconGUI(this.title, this.guiItems, this.listeners, this.metadata, this.patterns);
        }

    }

}
