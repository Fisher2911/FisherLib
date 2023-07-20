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

public class LoomGUI<P extends JavaPlugin> extends GUI<P> {

    private LoomGUI(
            String title,
            Map<GUISlot, GUIItem<P>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent, P>>, Consumer<? extends GUIEvent<? extends InventoryEvent, P>>> listeners,
            Metadata metadata,
            List<Pattern<P>> patterns
    ) {
        super(title, guiItems, listeners, Type.LOOM, metadata, patterns);
    }

    public @Nullable GUIItem<P> getBanner() {
        return this.getItem(GUISlot.Loom.BANNER);
    }

    public void setBanner(GUIItem<P> banner) {
        this.setItem(GUISlot.Loom.BANNER, banner);
    }

    public @Nullable GUIItem<P> getDye() {
        return this.getItem(GUISlot.Loom.DYE);
    }

    public void setDye(GUIItem<P> dye) {
        this.setItem(GUISlot.Loom.DYE, dye);
    }

    public @Nullable GUIItem<P> getPattern() {
        return this.getItem(GUISlot.Loom.PATTERN);
    }

    public void setPattern(GUIItem<P> pattern) {
        this.setItem(GUISlot.Loom.PATTERN, pattern);
    }

    public @Nullable GUIItem<P> getResult() {
        return this.getItem(GUISlot.Loom.RESULT);
    }

    public void setResult(GUIItem<P> result) {
        this.setItem(GUISlot.Loom.RESULT, result);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.LOOM, title);
        return this.inventory;
    }

    public static <P extends JavaPlugin> Builder<P> builder() {
        return new Builder<>();
    }

    public static class Builder<P extends JavaPlugin> extends GUI.Builder<Builder<P>, LoomGUI<P>, P> {

        protected Builder() {
        }

        public Builder<P> banner(GUIItem<P> banner) {
            this.guiItems.put(GUISlot.Loom.BANNER, banner);
            return this;
        }

        public Builder<P> dye(GUIItem<P> dye) {
            this.guiItems.put(GUISlot.Loom.DYE, dye);
            return this;
        }

        public Builder<P> pattern(GUIItem<P> pattern) {
            this.guiItems.put(GUISlot.Loom.PATTERN, pattern);
            return this;
        }

        public Builder<P> result(GUIItem<P> result) {
            this.guiItems.put(GUISlot.Loom.RESULT, result);
            return this;
        }

        public LoomGUI<P> build() {
            return new LoomGUI<>(this.title, this.guiItems, this.listeners, this.metadata, this.patterns);
        }

    }
    
}
