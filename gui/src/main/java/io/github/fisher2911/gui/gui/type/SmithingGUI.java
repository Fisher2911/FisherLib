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
public class SmithingGUI extends GUI {

    private SmithingGUI(
            String title,
            Map<GUISlot, GUIItem> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            Metadata metadata,
            List<Pattern> patterns
    ) {
        super(title, guiItems, listeners, Type.SMITHING_TABLE, metadata, patterns);
    }

    public @Nullable GUIItem getTemplate() {
        return this.getItem(GUISlot.SmithingTable.TEMPLATE);
    }

    public void setTemplate(GUIItem template) {
        this.setItem(GUISlot.SmithingTable.TEMPLATE, template);
    }

    public @Nullable GUIItem setBaseItem() {
        return this.getItem(GUISlot.SmithingTable.BASE_ITEM);
    }

    public void setBaseItem(GUIItem baseItem) {
        this.setItem(GUISlot.SmithingTable.BASE_ITEM, baseItem);
    }

    public @Nullable GUIItem getAdditionalItem() {
        return this.getItem(GUISlot.SmithingTable.ADDITIONAL_ITEM);
    }

    public void setAdditionalItem(GUIItem additionalItem) {
        this.setItem(GUISlot.SmithingTable.ADDITIONAL_ITEM, additionalItem);
    }

    public @Nullable GUIItem getResult() {
        return this.getItem(GUISlot.SmithingTable.RESULT);
    }

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

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder extends GUI.Builder<Builder, SmithingGUI> {

        protected Builder() {}

        public Builder template(GUIItem template) {
            this.guiItems.put(GUISlot.SmithingTable.TEMPLATE, template);
            return this;
        }

        public Builder baseItem(GUIItem baseItem) {
            this.guiItems.put(GUISlot.SmithingTable.BASE_ITEM, baseItem);
            return this;
        }

        public Builder additionalItem(GUIItem additionalItem) {
            this.guiItems.put(GUISlot.SmithingTable.ADDITIONAL_ITEM, additionalItem);
            return this;
        }

        public Builder result(GUIItem result) {
            this.guiItems.put(GUISlot.SmithingTable.RESULT, result);
            return this;
        }

        public SmithingGUI build() {
            return new SmithingGUI(this.title, this.guiItems, this.listeners, this.metadata, this.patterns);
        }

    }

}
