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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CartographyGUI<P extends JavaPlugin> extends GUI<P> {

    private CartographyGUI(
            String title,
            Map<GUISlot, GUIItem<P>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent, P>>, Consumer<? extends GUIEvent<? extends InventoryEvent, P>>> listeners,
            Metadata metadata,
            List<Pattern<P>> patterns
    ) {
        super(title, guiItems, listeners, Type.CARTOGRAPHY, metadata, patterns);
    }

    public @Nullable GUIItem<P> getMapItem() {
        return this.getItem(GUISlot.CartographyTable.MAP);
    }

    public void setMapItem(GUIItem<P> item) {
        this.setItem(GUISlot.CartographyTable.MAP, item);
    }

    public @Nullable GUIItem<P> getPaperItem() {
        return this.getItem(GUISlot.CartographyTable.PAPER);
    }

    public void setPaperItem(GUIItem<P> item) {
        this.setItem(GUISlot.CartographyTable.PAPER, item);
    }

    public @Nullable GUIItem<P> getOutputItem() {
        return this.getItem(GUISlot.CartographyTable.OUTPUT);
    }

    public void setOutputItem(GUIItem<P> item) {
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

    public static <P extends JavaPlugin> Builder<P> builder() {
        return new Builder<>();
    }

    public static class Builder<P extends JavaPlugin> extends GUI.Builder<Builder<P>, CartographyGUI<P>, P> {

        protected Builder() {
        }

        public Builder<P> mapItem(GUIItem<P> item) {
            this.guiItems.put(GUISlot.CartographyTable.MAP, item);
            return this;
        }

        public Builder<P> paperItem(GUIItem<P> item) {
            this.guiItems.put(GUISlot.CartographyTable.PAPER, item);
            return this;
        }

        public Builder<P> outputItem(GUIItem<P> item) {
            this.guiItems.put(GUISlot.CartographyTable.OUTPUT, item);
            return this;
        }

        public CartographyGUI<P> build() {
            return new CartographyGUI<>(this.title, this.guiItems, this.listeners, this.metadata, this.patterns);
        }

    }

}
