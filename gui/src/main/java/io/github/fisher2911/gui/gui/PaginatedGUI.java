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

package io.github.fisher2911.gui.gui;

import io.github.fisher2911.common.metadata.Metadata;
import io.github.fisher2911.common.placeholder.Placeholders;
import io.github.fisher2911.gui.event.GUIEvent;
import io.github.fisher2911.gui.gui.pattern.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A type of {@link GUI} that allows multiple pages of {@link GUI}s to be displayed to the {@link Player}.
 * These can be any type of {@link GUI}, excluding {@link PaginatedGUI}s.
 */
@SuppressWarnings("unused")
public class PaginatedGUI extends GUI {

    private final JavaPlugin plugin;
    private final GUIManager guiManager;
    private final List<GUI> pages;
    private int currentPage;
    private boolean switchingPages;

    public PaginatedGUI(
            GUIManager guiManager,
            String title,
            Map<GUISlot, GUIItem> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent>>, Consumer<? extends GUIEvent<? extends InventoryEvent>>> listeners,
            Type type,
            Metadata metadata,
            List<Pattern> patterns,
            List<GUI> pages
    ) {
        super(title, guiItems, listeners, type, metadata, patterns);
        this.plugin = JavaPlugin.getProvidingPlugin(this.getClass());
        this.guiManager = guiManager;
        this.pages = pages;
        this.pages.forEach(page -> page.setOwner(this));
        this.currentPage = 0;
        this.setTitle(this.getCurrentGUI().getTitle());
        this.refillGUIItems();
    }

    @Override
    protected Inventory createInventory(String title) {
        final GUI page = this.getCurrentGUI();
        this.inventory = page.getInventory();
        return this.inventory;
    }

    /**
     * This changes the inventory to the next page, or stays the same if it is already on the last page
     */
    public void nextPage() {
        final int previousPage = this.currentPage;
        final Collection<Player> viewers = new HashSet<>(this.getViewers());
        this.currentPage = Math.min(this.pages.size() - 1, this.currentPage + 1);
        if (previousPage != this.currentPage) {
            this.pages.get(previousPage).removeViewers(viewers);
            this.createInventory(this.getCurrentGUI().getTitle());
            this.refillGUIItems();
        }
        this.openGUIS(viewers);
    }

    /**
     * This changes the inventory to the previous page, or stays the same if it is already on the first page
     */
    public void previousPage() {
        final int previousPage = this.currentPage;
        final Collection<Player> viewers = new HashSet<>(this.getViewers());
        this.currentPage = Math.max(0, this.currentPage - 1);
        if (previousPage != currentPage) {
            this.pages.get(previousPage).removeViewers(viewers);
            this.createInventory(this.getCurrentGUI().getTitle());
            this.refillGUIItems();
        }
        this.openGUIS(viewers);
    }

    /**
     * @return The current page of the {@link PaginatedGUI}
     */
    public GUI getCurrentGUI() {
        return this.pages.get(this.currentPage);
    }

    private void refillGUIItems() {
        this.clearItems();
        final GUI gui = this.getCurrentGUI();
        gui.getGUIItems().forEach(this::setItem);
    }

    private void openGUIS(Collection<Player> viewers) {
        this.switchingPages = true;
        this.populate();
        viewers.forEach(player -> this.guiManager.openPaginatedGUI(this, viewers));
        this.switchingPages = false;
    }

    @Override
    public void populate() {
        this.getCurrentGUI().populate();
        // refill after populate to ensure that the pattern items are added
        this.refillGUIItems();
        super.populate();
    }

    @Override
    public void populate(Placeholders placeholders, Object... parsedPlaceholders) {
        this.getCurrentGUI().populate(placeholders, parsedPlaceholders);
        // refill after populate to ensure that the pattern items are added
        this.refillGUIItems();
        super.populate(placeholders, parsedPlaceholders);
    }

    public void setItem(GUISlot slot, GUIItem guiItem) {
        this.getCurrentGUI().setItem(slot, guiItem);
        super.setItem(slot, guiItem);
    }

    public void setItem(GUISlot slot, GUIItem guiItem, Placeholders placeholders, Object... parsePlaceholders) {
        this.getCurrentGUI().setItem(slot, guiItem, placeholders, parsePlaceholders);
        super.setItem(slot, guiItem, placeholders, parsePlaceholders);
    }

    /**
     * @param gui The {@link GUI} that is being checked
     * @return The index of the {@link GUI} in the {@link PaginatedGUI}
     */
    public int getPageIndex(GUI gui) {
        return this.pages.indexOf(gui);
    }

    /**
     * @return The amount of pages in the {@link PaginatedGUI}
     */
    public int getPageSize() {
        return this.pages.size();
    }

    @Override
    public GUISlot getDefaultPaginatedPreviousPageSlot() {
        return this.getCurrentGUI().getDefaultPaginatedPreviousPageSlot();
    }

    @Override
    public GUISlot getDefaultPaginatedNextPageSlot() {
        return this.getCurrentGUI().getDefaultPaginatedNextPageSlot();
    }

    /**
     * @return Whether the {@link PaginatedGUI} is currently switching pages
     */
    public boolean isSwitchingPages() {
        return this.switchingPages;
    }

    /**
     * @param guiManager The {@link GUIManager} that is managing the {@link PaginatedGUI}
     * @return A {@link PaginatedGUI.Builder} to create a {@link PaginatedGUI}
     */
    public static PaginatedGUI.Builder builder(GUIManager guiManager) {
        return new PaginatedGUI.Builder(guiManager, new ArrayList<>());
    }

    @Override
    public @Unmodifiable Set<Player> getViewers() {
        return this.getCurrentGUI().getViewers();
    }

    @Override
    public void removeViewer(Player player) {
        super.removeViewer(player);
        this.pages.forEach(page -> page.removeViewer(player));
    }

    /**
     * @return An unmodifiable list of the {@link GUI}s in the {@link PaginatedGUI}
     */
    @Unmodifiable
    public List<GUI> getPages() {
        return Collections.unmodifiableList(this.pages);
    }

    public static class Builder extends GUI.Builder<PaginatedGUI.Builder, PaginatedGUI> {

        private final GUIManager guiManager;
        private final List<GUI> pages;

        private Builder(GUIManager guiManager, List<GUI> pages) {
            this.guiManager = guiManager;
            this.pages = pages;
        }

        /**
         * @param page The {@link GUI} to add to the {@link PaginatedGUI}
         * @return The {@link PaginatedGUI.Builder} instance
         */
        public PaginatedGUI.Builder addPage(GUI page) {
            this.pages.add(page);
            return this;
        }

        /**
         * @param pages The {@link GUI}s to add to the {@link PaginatedGUI}
         * @return The {@link PaginatedGUI.Builder} instance
         */
        public PaginatedGUI.Builder addPages(List<GUI> pages) {
            this.pages.addAll(pages);
            return this;
        }

        /**
         * This adds {@link Pattern}s to all of the {@link GUI}s in the {@link PaginatedGUI}
         *
         * @param patterns The {@link Pattern}s to add
         * @return The {@link PaginatedGUI.Builder} instance
         */
        public PaginatedGUI.Builder pagePatterns(List<Pattern> patterns) {
            this.pages.forEach(page -> page.addPatterns(patterns));
            return this;
        }

        /**
         *
         * @return A new {@link PaginatedGUI} instance
         */
        @Override
        public PaginatedGUI build() {
            return new PaginatedGUI(
                    this.guiManager,
                    this.title,
                    this.guiItems,
                    this.listeners,
                    Type.PAGINATED,
                    this.metadata,
                    this.patterns,
                    this.pages
            );
        }

    }

}
