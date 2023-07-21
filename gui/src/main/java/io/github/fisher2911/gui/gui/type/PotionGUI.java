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

public class PotionGUI<P extends JavaPlugin> extends GUI<P> {

    private PotionGUI(
            String title,
            Map<GUISlot, GUIItem<P>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent, P>>, Consumer<? extends GUIEvent<? extends InventoryEvent, P>>> listeners,
            Metadata metadata,
            List<Pattern<P>> patterns
    ) {
        super(title, guiItems, listeners, Type.BREWING_STAND, metadata, patterns);
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.BREWING, title);
        return this.inventory;
    }

    public void setFuel(GUIItem<P> item) {
        this.setItem(GUISlot.Potion.FUEL, item);
    }

    public void setIngredient(GUIItem<P> item) {
        this.setItem(GUISlot.Potion.FUEL, item);
    }

    public void setZero(GUIItem<P> item) {
        this.setItem(GUISlot.Potion.ZERO, item);
    }

    public void setOne(GUIItem<P> item) {
        this.setItem(GUISlot.Potion.ONE, item);
    }

    public void setTwo(GUIItem<P> item) {
        this.setItem(GUISlot.Potion.TWO, item);
    }

    public @Nullable GUIItem<P> getFuel() {
        return this.getItem(GUISlot.Potion.FUEL);
    }

    public @Nullable GUIItem<P> getIngredient() {
        return this.getItem(GUISlot.Potion.INGREDIENT);
    }

    public @Nullable GUIItem<P> getZero() {
        return this.getItem(GUISlot.Potion.ZERO);
    }

    public @Nullable GUIItem<P> getOne() {
        return this.getItem(GUISlot.Potion.ONE);
    }

    public @Nullable GUIItem<P> getTwo() {
        return this.getItem(GUISlot.Potion.TWO);
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.Potion.ZERO;
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.Potion.TWO;
    }

    public static <P extends JavaPlugin> Builder<P> builder() {
        return new Builder<>();
    }

    public static class Builder<P extends JavaPlugin> extends GUI.Builder<Builder<P>, PotionGUI<P>, P> {

        protected Builder() {
        }

        public Builder<P> fuel(GUIItem<P> item) {
            this.guiItems.put(GUISlot.Potion.FUEL, item);
            return this;
        }

        public Builder<P> ingredient(GUIItem<P> item) {
            this.guiItems.put(GUISlot.Potion.INGREDIENT, item);
            return this;
        }

        public Builder<P> zero(GUIItem<P> item) {
            this.guiItems.put(GUISlot.Potion.ZERO, item);
            return this;
        }

        public Builder<P> one(GUIItem<P> item) {
            this.guiItems.put(GUISlot.Potion.ONE, item);
            return this;
        }

        public Builder<P> two(GUIItem<P> item) {
            this.guiItems.put(GUISlot.Potion.TWO, item);
            return this;
        }

        @Override
        public PotionGUI<P> build() {
            return new PotionGUI<>(
                    this.title,
                    this.guiItems,
                    this.listeners,
                    this.metadata,
                    this.patterns
            );
        }

    }

}
