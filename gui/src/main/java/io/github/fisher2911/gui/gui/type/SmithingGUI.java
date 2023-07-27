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
 * A {@link GUI} with an {@link GUI.Type#SMITHING_TABLE} {@link GUI.Type}.
 */
@SuppressWarnings("unused")
public class SmithingGUI extends GUI {

    private SmithingGUI(
            String title,
            List<Map<GUISlot, GUIItem>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            Metadata metadata,
            List<Pattern> patterns,
            boolean expandable
    ) {
        super(title, guiItems, listeners, Type.SMITHING_TABLE, metadata, patterns, expandable);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.SmithingTable#TEMPLATE} {@link GUISlot}.
     */
    public @Nullable GUIItem getTemplate() {
        return this.getItem(GUISlot.SmithingTable.TEMPLATE);
    }

    /**
     * @param template The {@link GUIItem} to set in the {@link GUISlot.SmithingTable#TEMPLATE} {@link GUISlot}.
     */
    public void setTemplate(GUIItem template) {
        this.setItem(GUISlot.SmithingTable.TEMPLATE, template);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.SmithingTable#BASE_ITEM} {@link GUISlot}.
     */
    public @Nullable GUIItem setBaseItem() {
        return this.getItem(GUISlot.SmithingTable.BASE_ITEM);
    }

    /**
     * @param baseItem The {@link GUIItem} to set in the {@link GUISlot.SmithingTable#BASE_ITEM} {@link GUISlot}.
     */
    public void setBaseItem(GUIItem baseItem) {
        this.setItem(GUISlot.SmithingTable.BASE_ITEM, baseItem);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.SmithingTable#ADDITIONAL_ITEM} {@link GUISlot}.
     */
    public @Nullable GUIItem getAdditionalItem() {
        return this.getItem(GUISlot.SmithingTable.ADDITIONAL_ITEM);
    }

    /**
     * @param additionalItem The {@link GUIItem} to set in the {@link GUISlot.SmithingTable#ADDITIONAL_ITEM} {@link GUISlot}.
     */
    public void setAdditionalItem(GUIItem additionalItem) {
        this.setItem(GUISlot.SmithingTable.ADDITIONAL_ITEM, additionalItem);
    }

    /**
     *
     * @return The {@link GUIItem} in the {@link GUISlot.SmithingTable#RESULT} {@link GUISlot}.
     */
    public @Nullable GUIItem getResult() {
        return this.getItem(GUISlot.SmithingTable.RESULT);
    }

    /**
     *
     * @param result The {@link GUIItem} to set in the {@link GUISlot.SmithingTable#RESULT} {@link GUISlot}.
     */
    public void setResult(GUIItem result) {
        this.setItem(GUISlot.SmithingTable.RESULT, result);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.SMITHING, title);
        return this.inventory;
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.SmithingTable.BASE_ITEM;
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.SmithingTable.ADDITIONAL_ITEM;
    }

    /**
     *
     * @return A new {@link Builder} for {@link SmithingGUI}s.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends GUI.Builder<Builder, SmithingGUI> {

        protected Builder() {
        }

        /**
         *
         * @param template The {@link GUIItem} to set in the {@link GUISlot.SmithingTable#TEMPLATE} {@link GUISlot}.
         * @return This {@link Builder}.
         */
        public Builder template(GUIItem template) {
            this.currentGuiItems.put(GUISlot.SmithingTable.TEMPLATE, template);
            return this;
        }

        /**
         *
         * @param baseItem The {@link GUIItem} to set in the {@link GUISlot.SmithingTable#BASE_ITEM} {@link GUISlot}.
         * @return This {@link Builder}.
         */
        public Builder baseItem(GUIItem baseItem) {
            this.currentGuiItems.put(GUISlot.SmithingTable.BASE_ITEM, baseItem);
            return this;
        }

        /**
         *
         * @param additionalItem The {@link GUIItem} to set in the {@link GUISlot.SmithingTable#ADDITIONAL_ITEM} {@link GUISlot}.
         * @return This {@link Builder}.
         */
        public Builder additionalItem(GUIItem additionalItem) {
            this.currentGuiItems.put(GUISlot.SmithingTable.ADDITIONAL_ITEM, additionalItem);
            return this;
        }

        /**
         *
         * @param result The {@link GUIItem} to set in the {@link GUISlot.SmithingTable#RESULT} {@link GUISlot}.
         * @return This {@link Builder}.
         */
        public Builder result(GUIItem result) {
            this.currentGuiItems.put(GUISlot.SmithingTable.RESULT, result);
            return this;
        }

        /**
         *
         * @return A new {@link SmithingGUI} with the properties set in this {@link Builder}.
         */
        public SmithingGUI build() {
            return new SmithingGUI(this.title, this.guiItems, this.listeners, this.metadata, this.patterns, this.expandable);
        }

    }

}
