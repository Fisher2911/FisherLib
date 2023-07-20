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

public class EnchantingGUI<P extends JavaPlugin> extends GUI<P> {

    private EnchantingGUI(
            String title,
            Map<GUISlot, GUIItem<P>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent, P>>, Consumer<? extends GUIEvent<? extends InventoryEvent, P>>> listeners,
            Metadata metadata,
            List<Pattern<P>> patterns
    ) {
        super(title, guiItems, listeners, Type.ENCHANTING, metadata, patterns);
    }

    public @Nullable GUIItem<P> getItem() {
        return this.getItem(GUISlot.EnchantingTable.ITEM);
    }

    public void setItem(GUIItem<P> item) {
        this.setItem(GUISlot.EnchantingTable.ITEM, item);
    }

    public @Nullable GUIItem<P> getLapisItem() {
        return this.getItem(GUISlot.EnchantingTable.LAPIS);
    }

    public void setLapisItem(GUIItem<P> item) {
        this.setItem(GUISlot.EnchantingTable.LAPIS, item);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.ENCHANTING, title);
        return this.inventory;
    }

    public static <P extends JavaPlugin> Builder<P> builder() {
        return new Builder<>();
    }

    public static class Builder<P extends JavaPlugin> extends GUI.Builder<Builder<P>, EnchantingGUI<P>, P> {

        protected Builder() {
        }

        public Builder<P> item(GUIItem<P> item) {
            return this.set(GUISlot.EnchantingTable.ITEM, item);
        }

        public Builder<P> lapis(GUIItem<P> item) {
            return this.set(GUISlot.EnchantingTable.LAPIS, item);
        }

        public EnchantingGUI<P> build() {
            return new EnchantingGUI<>(this.title, this.guiItems, this.listeners, this.metadata, this.patterns);
        }

    }

}
