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
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ChestGUI<P extends JavaPlugin> extends GUI<P> {

    private final int rows;

    private ChestGUI(
            String title,
            Map<GUISlot, GUIItem<P>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent, P>>, Consumer<? extends GUIEvent<? extends InventoryEvent, P>>> listeners,
            int rows,
            Metadata metadata,
            List<Pattern<P>> patterns
    ) {
        super(title, guiItems, listeners, Type.CHEST, metadata, patterns);
        this.rows = rows;
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        return this.inventory;
    }

    public int getRows() {
        return this.rows;
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.of(this.rows * 9 - 8);
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.of(this.rows * 9 - 1);
    }

    public static <P extends JavaPlugin> Builder<P> builder() {
        return new Builder<>();
    }

    public static class Builder<P extends JavaPlugin> extends GUI.Builder<Builder<P>, ChestGUI<P>, P> {

        private int rows = 1;

        protected Builder() {
        }

        public Builder<P> rows(int rows) {
            this.rows = rows;
            return this;
        }

        public ChestGUI<P> build() {
            return new ChestGUI<>(this.title, this.guiItems, this.listeners, this.rows, this.metadata, this.patterns);
        }

    }
    
}
