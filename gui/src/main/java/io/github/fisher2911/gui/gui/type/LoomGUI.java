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
 * A {@link GUI} with an {@link GUI.Type#LOOM} {@link GUI.Type}.
 */
@SuppressWarnings("unused")
public class LoomGUI extends GUI {

    private LoomGUI(
            String title,
            List<Map<GUISlot, GUIItem>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            Metadata metadata,
            List<Pattern> patterns,
            boolean expandable
    ) {
        super(title, guiItems, listeners, Type.LOOM, metadata, patterns, expandable);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.Loom#BANNER} slot.
     */
    public @Nullable GUIItem getBanner() {
        return this.getItem(GUISlot.Loom.BANNER);
    }

    /**
     * @param banner The {@link GUIItem} to set in the {@link GUISlot.Loom#BANNER} slot.
     */
    public void setBanner(GUIItem banner) {
        this.setItem(GUISlot.Loom.BANNER, banner);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.Loom#DYE} slot.
     */
    public @Nullable GUIItem getDye() {
        return this.getItem(GUISlot.Loom.DYE);
    }

    /**
     * @param dye The {@link GUIItem} to set in the {@link GUISlot.Loom#DYE} slot.
     */
    public void setDye(GUIItem dye) {
        this.setItem(GUISlot.Loom.DYE, dye);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.Loom#PATTERN} slot.
     */
    public @Nullable GUIItem getPattern() {
        return this.getItem(GUISlot.Loom.PATTERN);
    }

    /**
     * @param pattern The {@link GUIItem} to set in the {@link GUISlot.Loom#PATTERN} slot.
     */
    public void setPattern(GUIItem pattern) {
        this.setItem(GUISlot.Loom.PATTERN, pattern);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.Loom#RESULT} slot.
     */
    public @Nullable GUIItem getResult() {
        return this.getItem(GUISlot.Loom.RESULT);
    }

    /**
     * @param result The {@link GUIItem} to set in the {@link GUISlot.Loom#RESULT} slot.
     */
    public void setResult(GUIItem result) {
        this.setItem(GUISlot.Loom.RESULT, result);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.LOOM, title);
        return this.inventory;
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.Loom.BANNER;
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.Loom.PATTERN;
    }

    /**
     * @return A new {@link Builder} instance.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends GUI.Builder<Builder, LoomGUI> {

        protected Builder() {
        }

        /**
         * @param banner The {@link GUIItem} to set in the {@link GUISlot.Loom#BANNER} slot.
         * @return This {@link Builder} instance.
         */
        public Builder banner(GUIItem banner) {
            this.currentGuiItems.put(GUISlot.Loom.BANNER, banner);
            return this;
        }

        /**
         * @param dye The {@link GUIItem} to set in the {@link GUISlot.Loom#DYE} slot.
         * @return This {@link Builder} instance.
         */
        public Builder dye(GUIItem dye) {
            this.currentGuiItems.put(GUISlot.Loom.DYE, dye);
            return this;
        }

        /**
         * @param pattern The {@link GUIItem} to set in the {@link GUISlot.Loom#PATTERN} slot.
         * @return This {@link Builder} instance.
         */
        public Builder pattern(GUIItem pattern) {
            this.currentGuiItems.put(GUISlot.Loom.PATTERN, pattern);
            return this;
        }

        /**
         * @param result The {@link GUIItem} to set in the {@link GUISlot.Loom#RESULT} slot.
         * @return This {@link Builder} instance.
         */
        public Builder result(GUIItem result) {
            this.currentGuiItems.put(GUISlot.Loom.RESULT, result);
            return this;
        }

        /**
         * @return A new {@link LoomGUI} instance.
         */
        public LoomGUI build() {
            return new LoomGUI(this.title, this.guiItems, this.listeners, this.metadata, this.patterns, this.expandable);
        }

    }

}
