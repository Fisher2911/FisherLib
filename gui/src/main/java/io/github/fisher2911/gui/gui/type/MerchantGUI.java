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
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MerchantGUI<P extends JavaPlugin> extends GUI<P> {

    private final List<MerchantRecipe> trades;

    private MerchantGUI(
            String title,
            Map<GUISlot, GUIItem<P>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent, P>>, Consumer<? extends GUIEvent<? extends InventoryEvent, P>>> listeners,
            List<MerchantRecipe> trades,
            Metadata metadata,
            List<Pattern<P>> patterns
    ) {
        super(title, guiItems, listeners, Type.MERCHANT, metadata, patterns);
        this.trades = trades;
    }

    public @Nullable GUIItem<P> getFirst() {
        return this.getItem(GUISlot.Merchant.FIRST);
    }

    public @Nullable GUIItem<P> getSecond() {
        return this.getItem(GUISlot.Merchant.SECOND);
    }

    public @Nullable GUIItem<P> getResult() {
        return this.getItem(GUISlot.Merchant.RESULT);
    }

    public void addTrade(MerchantRecipe recipe) {
        this.trades.add(recipe);
    }

    public void removeTrade(MerchantRecipe recipe) {
        this.trades.remove(recipe);
    }

    public void setTrade(int index, MerchantRecipe recipe) {
        this.trades.set(index, recipe);
    }

    public void removeTrade(int index) {
        this.trades.remove(index);
    }

    @Unmodifiable
    public List<MerchantRecipe> getTrades() {
        return Collections.unmodifiableList(this.trades);
    }

    public void clearTrades() {
        this.trades.clear();
    }

    @Override
    protected Inventory createInventory(String title) {
        this.inventory = Bukkit.createInventory(null, InventoryType.MERCHANT, title);
        return this.inventory;
    }

    public static <P extends JavaPlugin> Builder<P> builder() {
        return new Builder<>();
    }

    public static class Builder<P extends JavaPlugin> extends GUI.Builder<Builder<P>, MerchantGUI<P>, P> {

        private final List<MerchantRecipe> recipes;

        protected Builder() {
            this.recipes = new ArrayList<>();
        }

        public Builder<P> first(GUIItem<P> item) {
            this.guiItems.put(GUISlot.Merchant.FIRST, item);
            return this;
        }

        public Builder<P> second(GUIItem<P> item) {
            this.guiItems.put(GUISlot.Merchant.SECOND, item);
            return this;
        }

        public Builder<P> result(GUIItem<P> item) {
            this.guiItems.put(GUISlot.Merchant.RESULT, item);
            return this;
        }

        public Builder<P> trade(MerchantRecipe recipe) {
            this.recipes.add(recipe);
            return this;
        }

        public MerchantGUI<P> build() {
            return new MerchantGUI<>(this.title, this.guiItems, this.listeners, this.recipes, this.metadata, this.patterns);
        }

    }

}
