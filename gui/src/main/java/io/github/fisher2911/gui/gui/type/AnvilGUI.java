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
public class AnvilGUI extends GUI {

    private AnvilGUI(
            String title,
            Map<GUISlot, GUIItem> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            Metadata metadata,
            List<Pattern> patterns
    ) {
        super(title, guiItems, listeners, Type.ANVIL, metadata, patterns);
    }

    public @Nullable GUIItem getFirst() {
        return this.getItem(GUISlot.Anvil.FIRST);
    }

    public void setFirst(GUIItem item) {
        this.setItem(GUISlot.Anvil.FIRST, item);
    }

    public @Nullable GUIItem getSecond() {
        return this.getItem(GUISlot.Anvil.SECOND);
    }

    public void setSecond(GUIItem item) {
        this.setItem(GUISlot.Anvil.SECOND, item);
    }

    public @Nullable GUIItem getResult() {
        return this.getItem(GUISlot.Anvil.RESULT);
    }

    public void setResult(GUIItem item) {
        this.setItem(GUISlot.Anvil.RESULT, item);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.ANVIL, title);
        return this.inventory;
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.Anvil.FIRST;
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.Anvil.SECOND;
    }

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder extends GUI.Builder<Builder, AnvilGUI> {

        protected Builder() {
        }

        public Builder first(GUIItem item) {
            this.guiItems.put(GUISlot.Anvil.FIRST, item);
            return this;
        }

        public Builder second(GUIItem item) {
            this.guiItems.put(GUISlot.Anvil.SECOND, item);
            return this;
        }

        public Builder result(GUIItem item) {
            this.guiItems.put(GUISlot.Anvil.RESULT, item);
            return this;
        }

        public AnvilGUI build() {
            return new AnvilGUI(this.title, this.guiItems, this.listeners, this.metadata, this.patterns);
        }

    }

}
