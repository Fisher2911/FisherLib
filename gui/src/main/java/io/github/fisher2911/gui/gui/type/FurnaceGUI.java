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

@SuppressWarnings("unused")
public class FurnaceGUI extends GUI {

    private FurnaceGUI(
            String title,
            Map<GUISlot, GUIItem> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            Metadata metadata,
            List<Pattern> patterns
    ) {
        super(title, guiItems, listeners, Type.FURNACE, metadata, patterns);
    }

    public @Nullable GUIItem getSmelting() {
        return this.getItem(GUISlot.Furnace.SMELTING);
    }

    public void setSmelting(GUIItem item) {
        this.setItem(GUISlot.Furnace.SMELTING, item);
    }

    public @Nullable GUIItem getFuel() {
        return this.getItem(GUISlot.Furnace.FUEL);
    }

    public void setFuel(GUIItem item) {
        this.setItem(GUISlot.Furnace.FUEL, item);
    }

    public @Nullable GUIItem getResult() {
        return this.getItem(GUISlot.Furnace.RESULT);
    }

    public void setResult(GUIItem item) {
        this.setItem(GUISlot.Furnace.RESULT, item);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.FURNACE, title);
        return this.inventory;
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.Furnace.SMELTING;
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.Furnace.FUEL;
    }

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder extends GUI.Builder<Builder, FurnaceGUI> {

        protected Builder() {
        }

        public Builder smelting(GUIItem item) {
            this.set(GUISlot.Furnace.SMELTING, item);
            return this;
        }

        public Builder fuel(GUIItem item) {
            this.set(GUISlot.Furnace.FUEL, item);
            return this;
        }

        public Builder result(GUIItem item) {
            this.set(GUISlot.Furnace.RESULT, item);
            return this;
        }

        public FurnaceGUI build() {
            return new FurnaceGUI(this.title, this.guiItems, this.listeners, this.metadata, this.patterns);
        }

    }

}
