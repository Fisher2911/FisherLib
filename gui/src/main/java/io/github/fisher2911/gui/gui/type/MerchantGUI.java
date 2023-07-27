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

/**
 * A {@link GUI} with an {@link GUI.Type#MERCHANT} {@link GUI.Type}.
 */
@SuppressWarnings("unused")
public class MerchantGUI extends GUI {

    private final List<MerchantRecipe> trades;

    private MerchantGUI(
            String title,
            List<Map<GUISlot, GUIItem>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            List<MerchantRecipe> trades,
            Metadata metadata,
            List<Pattern> patterns,
            boolean expandable
    ) {
        super(title, guiItems, listeners, Type.MERCHANT, metadata, patterns, expandable);
        this.trades = trades;
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.Merchant#FIRST} slot.
     */
    public @Nullable GUIItem getFirst() {
        return this.getItem(GUISlot.Merchant.FIRST);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.Merchant#SECOND} slot.
     */
    public @Nullable GUIItem getSecond() {
        return this.getItem(GUISlot.Merchant.SECOND);
    }

    /**
     * @return The {@link GUIItem} in the {@link GUISlot.Merchant#RESULT} slot.
     */
    public @Nullable GUIItem getResult() {
        return this.getItem(GUISlot.Merchant.RESULT);
    }

    /**
     * @param recipe The {@link MerchantRecipe} to add to the {@link MerchantGUI}.
     */
    public void addTrade(MerchantRecipe recipe) {
        this.trades.add(recipe);
    }

    /**
     * @param recipe The {@link MerchantRecipe} to remove from the {@link MerchantGUI}.
     */
    public void removeTrade(MerchantRecipe recipe) {
        this.trades.remove(recipe);
    }

    /**
     * @param index  The index of the {@link MerchantRecipe} to set.
     * @param recipe The {@link MerchantRecipe} to set.
     */
    public void setTrade(int index, MerchantRecipe recipe) {
        this.trades.set(index, recipe);
    }

    /**
     * @param index The index of the {@link MerchantRecipe} to remove.
     */
    public void removeTrade(int index) {
        this.trades.remove(index);
    }

    /**
     * @return An unmodifiable {@link List} of {@link MerchantRecipe}s.
     */
    @Unmodifiable
    public List<MerchantRecipe> getTrades() {
        return Collections.unmodifiableList(this.trades);
    }

    /**
     * Clears all {@link MerchantRecipe}s from the {@link MerchantGUI}.
     */
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

    /**
     * @return A new {@link Builder} for {@link MerchantGUI}s.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends GUI.Builder<Builder, MerchantGUI> {

        private final List<MerchantRecipe> recipes;

        protected Builder() {
            this.recipes = new ArrayList<>();
        }

        /**
         * @param item The {@link GUIItem} to set in the {@link GUISlot.Merchant#FIRST} slot.
         * @return The {@link Builder}.
         */
        public Builder first(GUIItem item) {
            this.currentGuiItems.put(GUISlot.Merchant.FIRST, item);
            return this;
        }

        /**
         * @param item The {@link GUIItem} to set in the {@link GUISlot.Merchant#SECOND} slot.
         * @return The {@link Builder}.
         */
        public Builder second(GUIItem item) {
            this.currentGuiItems.put(GUISlot.Merchant.SECOND, item);
            return this;
        }

        /**
         * @param item The {@link GUIItem} to set in the {@link GUISlot.Merchant#RESULT} slot.
         * @return The {@link Builder}.
         */
        public Builder result(GUIItem item) {
            this.currentGuiItems.put(GUISlot.Merchant.RESULT, item);
            return this;
        }

        /**
         * @param recipe The {@link MerchantRecipe} to add to the {@link MerchantGUI}.
         * @return The {@link Builder}.
         */
        public Builder trade(MerchantRecipe recipe) {
            this.recipes.add(recipe);
            return this;
        }

        /**
         * @return The {@link MerchantGUI} built from the {@link Builder}.
         */
        public MerchantGUI build() {
            return new MerchantGUI(this.title, this.guiItems, this.listeners, this.recipes, this.metadata, this.patterns, this.expandable);
        }

    }

}
