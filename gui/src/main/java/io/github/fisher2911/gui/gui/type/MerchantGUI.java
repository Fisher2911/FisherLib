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
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class MerchantGUI extends GUI {

    private final List<MerchantRecipe> trades;

    private MerchantGUI(
            String title,
            Map<GUISlot, GUIItem> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            List<MerchantRecipe> trades,
            Metadata metadata,
            List<Pattern> patterns
    ) {
        super(title, guiItems, listeners, Type.MERCHANT, metadata, patterns);
        this.trades = trades;
    }

    public @Nullable GUIItem getFirst() {
        return this.getItem(GUISlot.Merchant.FIRST);
    }

    public @Nullable GUIItem getSecond() {
        return this.getItem(GUISlot.Merchant.SECOND);
    }

    public @Nullable GUIItem getResult() {
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

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return GUISlot.Merchant.FIRST;
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return GUISlot.Merchant.SECOND;
    }

    public static  Builder builder() {
        return new Builder();
    }

    public static class Builder extends GUI.Builder<Builder, MerchantGUI> {

        private final List<MerchantRecipe> recipes;

        protected Builder() {
            this.recipes = new ArrayList<>();
        }

        public Builder first(GUIItem item) {
            this.guiItems.put(GUISlot.Merchant.FIRST, item);
            return this;
        }

        public Builder second(GUIItem item) {
            this.guiItems.put(GUISlot.Merchant.SECOND, item);
            return this;
        }

        public Builder result(GUIItem item) {
            this.guiItems.put(GUISlot.Merchant.RESULT, item);
            return this;
        }

        public Builder trade(MerchantRecipe recipe) {
            this.recipes.add(recipe);
            return this;
        }

        public MerchantGUI build() {
            return new MerchantGUI(this.title, this.guiItems, this.listeners, this.recipes, this.metadata, this.patterns);
        }

    }

}
