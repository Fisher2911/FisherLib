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

public class SmithingGUI<P extends JavaPlugin> extends GUI<P> {

    private SmithingGUI(
            String title,
            Map<GUISlot, GUIItem<P>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent, P>>, Consumer<? extends GUIEvent<? extends InventoryEvent, P>>> listeners,
            Metadata metadata,
            List<Pattern<P>> patterns
    ) {
        super(title, guiItems, listeners, Type.SMITHING_TABLE, metadata, patterns);
    }

    public @Nullable GUIItem<P> getTemplate() {
        return this.getItem(GUISlot.SmithingTable.TEMPLATE);
    }

    public void setTemplate(GUIItem<P> template) {
        this.setItem(GUISlot.SmithingTable.TEMPLATE, template);
    }

    public @Nullable GUIItem<P> setBaseItem() {
        return this.getItem(GUISlot.SmithingTable.BASE_ITEM);
    }

    public void setBaseItem(GUIItem<P> baseItem) {
        this.setItem(GUISlot.SmithingTable.BASE_ITEM, baseItem);
    }

    public @Nullable GUIItem<P> getAdditionalItem() {
        return this.getItem(GUISlot.SmithingTable.ADDITIONAL_ITEM);
    }

    public void setAdditionalItem(GUIItem<P> additionalItem) {
        this.setItem(GUISlot.SmithingTable.ADDITIONAL_ITEM, additionalItem);
    }

    public @Nullable GUIItem<P> getResult() {
        return this.getItem(GUISlot.SmithingTable.RESULT);
    }

    public void setResult(GUIItem<P> result) {
        this.setItem(GUISlot.SmithingTable.RESULT, result);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.SMITHING, title);
        return this.inventory;
    }

    public static <P extends JavaPlugin> Builder<P> builder() {
        return new Builder<>();
    }

    public static class Builder<P extends JavaPlugin> extends GUI.Builder<Builder<P>, SmithingGUI<P>, P> {

        protected Builder() {}

        public Builder<P> template(GUIItem<P> template) {
            this.guiItems.put(GUISlot.SmithingTable.TEMPLATE, template);
            return this;
        }

        public Builder<P> baseItem(GUIItem<P> baseItem) {
            this.guiItems.put(GUISlot.SmithingTable.BASE_ITEM, baseItem);
            return this;
        }

        public Builder<P> additionalItem(GUIItem<P> additionalItem) {
            this.guiItems.put(GUISlot.SmithingTable.ADDITIONAL_ITEM, additionalItem);
            return this;
        }

        public Builder<P> result(GUIItem<P> result) {
            this.guiItems.put(GUISlot.SmithingTable.RESULT, result);
            return this;
        }

        public SmithingGUI<P> build() {
            return new SmithingGUI<>(this.title, this.guiItems, this.listeners, this.metadata, this.patterns);
        }

    }

}
