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

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A {@link GUI} with an {@link GUI.Type#CHEST} {@link GUI.Type}.
 */
@SuppressWarnings("unused")
public class ChestGUI extends GUI {

    private final int rows;

    private ChestGUI(
            String title,
            List<Map<GUISlot, GUIItem>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            int rows,
            Metadata metadata,
            List<Pattern> patterns,
            boolean expandable
    ) {
        super(title, guiItems, listeners, Type.CHEST, metadata, patterns, expandable);
        this.rows = rows;
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, rows * 9, title);
        return this.inventory;
    }

    /**
     *
     * @return The number of rows in this {@link ChestGUI}.
     */
    public int getRows() {
        return this.rows;
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.of(this.rows * 9 - 9);
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.of(this.rows * 9 - 1);
    }

    /**
     *
     * @return A new {@link Builder} for a {@link ChestGUI}.
     */
    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder extends GUI.Builder<Builder, ChestGUI> {

        private int rows = 1;

        protected Builder() {
        }

        /**
         *
         * @param rows The number of rows in the {@link ChestGUI}.
         * @return This {@link Builder}.
         */
        public Builder rows(int rows) {
            this.rows = rows;
            return this;
        }

        public ChestGUI build() {
            return new ChestGUI(this.title, this.guiItems, this.listeners, this.rows, this.metadata, this.patterns, this.expandable);
        }

    }
    
}
