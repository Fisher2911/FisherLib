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

public class GrindstoneGUI<P extends JavaPlugin> extends GUI<P> {

    private GrindstoneGUI(
            String title,
            Map<GUISlot, GUIItem<P>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent, P>>, Consumer<? extends GUIEvent<? extends InventoryEvent, P>>> listeners,
            Metadata metadata,
            List<Pattern<P>> patterns
    ) {
        super(title, guiItems, listeners, Type.GRINDSTONE, metadata, patterns);
    }

    public @Nullable GUIItem<P> getFirstItem() {
        return this.getItem(GUISlot.Grindstone.FIRST);
    }

    public void setFirstItem(GUIItem<P> item) {
        this.setItem(GUISlot.Grindstone.FIRST, item);
    }

    public @Nullable GUIItem<P> getSecondItem() {
        return this.getItem(GUISlot.Grindstone.SECOND);
    }

    public void setSecondItem(GUIItem<P> item) {
        this.setItem(GUISlot.Grindstone.SECOND, item);
    }

    public @Nullable GUIItem<P> getResultItem() {
        return this.getItem(GUISlot.Grindstone.RESULT);
    }

    public void setResultItem(GUIItem<P> item) {
        this.setItem(GUISlot.Grindstone.RESULT, item);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.GRINDSTONE, title);
        return this.inventory;
    }

    public static <P extends JavaPlugin> Builder<P> builder() {
        return new Builder<>();
    }

    public static class Builder<P extends JavaPlugin> extends GUI.Builder<Builder<P>, GrindstoneGUI<P>, P> {

        protected Builder() {
        }

        public Builder<P> firstItem(GUIItem<P> item) {
            this.guiItems.put(GUISlot.Grindstone.FIRST, item);
            return this;
        }

        public Builder<P> secondItem(GUIItem<P> item) {
            this.guiItems.put(GUISlot.Grindstone.SECOND, item);
            return this;
        }

        public Builder<P> resultItem(GUIItem<P> item) {
            this.guiItems.put(GUISlot.Grindstone.RESULT, item);
            return this;
        }

        public GrindstoneGUI<P> build() {
            return new GrindstoneGUI<>(this.title, this.guiItems, this.listeners, this.metadata, this.patterns);
        }

    }

}
