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
 * A {@link GUI} with an {@link GUI.Type#WORK_BENCH} {@link GUI.Type}.
 */
@SuppressWarnings("unused")
public class WorkBenchGUI extends GUI {

    private WorkBenchGUI(
            String title,
            Map<GUISlot, GUIItem> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            Metadata metadata,
            List<Pattern> patterns
    ) {
        super(title, guiItems, listeners, Type.WORK_BENCH, metadata, patterns);
    }

    /**
     * @param guiItem The {@link GUIItem} to set as the result.
     */
    public void setResult(GUIItem guiItem) {
        this.setItem(GUISlot.WorkBench.RESULT, guiItem);
    }

    /**
     * @return The {@link GUIItem} set as the result.
     */
    public @Nullable GUIItem getResult() {
        return this.getItem(GUISlot.WorkBench.RESULT);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.CRAFTING, title);
        return this.inventory;
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.of(1);
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.WorkBench.RESULT;
    }

    /**
     * @return A new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends GUI.Builder<Builder, WorkBenchGUI> {

        protected Builder() {
        }

        /**
         * @param guiItem The {@link GUIItem} to set as the result.
         * @return The {@link Builder} instance.
         */
        public Builder result(GUIItem guiItem) {
            this.guiItems.put(GUISlot.WorkBench.RESULT, guiItem);
            return this;
        }

        /**
         * @return The built {@link WorkBenchGUI} instance.
         */
        public WorkBenchGUI build() {
            return new WorkBenchGUI(this.title, this.guiItems, this.listeners, this.metadata, this.patterns);
        }

    }

}
