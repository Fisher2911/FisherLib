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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class PaginatedGUI<P extends JavaPlugin> extends GUI<P> {

    private final P plugin;
    private final GUIManager<P> guiManager;
    private final List<GUI<P>> pages;
    private int currentPage;
    private boolean switchingPages;

    public PaginatedGUI(
            P plugin,
            GUIManager<P> guiManager,
            String title,
            Map<GUISlot, GUIItem<P>> guiItems,
            Map<Class<? extends GUIEvent<? extends InventoryEvent, P>>, Consumer<? extends GUIEvent<? extends InventoryEvent, P>>> listeners,
            Type type,
            Metadata metadata,
            List<Pattern<P>> patterns,
            List<GUI<P>> pages
    ) {
        super(title, guiItems, listeners, type, metadata, patterns);
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.pages = pages;
        this.pages.forEach(page -> page.setOwner(this.plugin, this));
        this.currentPage = 0;
        this.setTitle(this.getCurrentGUI().getTitle());
        this.refillGUIItems();
    }

    @Override
    protected Inventory createInventory(String title) {
        final GUI<P> page = this.getCurrentGUI();
        this.inventory = page.createInventory(title);
        return this.inventory;
    }

    public void nextPage() {
        final int previousPage = this.currentPage;
        final Collection<Player> viewers = this.getViewers();
        this.currentPage = Math.min(this.pages.size() - 1, this.currentPage + 1);
        if (previousPage != this.currentPage) {
            this.refillGUIItems();
        }
        this.openGUIS(viewers);
    }

    public void previousPage() {
        final int previousPage = this.currentPage;
        final Collection<Player> viewers = this.getViewers();
        this.currentPage = Math.max(0, this.currentPage - 1);
        if (previousPage != currentPage) {
            this.refillGUIItems();
        }
        this.openGUIS(viewers);
    }

    public GUI<P> getCurrentGUI() {
        return this.pages.get(this.currentPage);
    }

    private void refillGUIItems() {
        this.clearItems();
        final GUI<P> gui = this.getCurrentGUI();
        gui.getGUIItems().forEach(this::setItem);
    }

    private void openGUIS(Collection<Player> viewers) {
        this.switchingPages = true;
        final GUI<P> gui = this.getCurrentGUI();
        gui.populate();
        this.refillGUIItems();
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

    public int getPageIndex(GUI<P> gui) {
        return this.pages.indexOf(gui);
    }

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

    public boolean isSwitchingPages() {
        return this.switchingPages;
    }

    public static <P extends JavaPlugin> PaginatedGUI.Builder<P> builder(P plugin, GUIManager<P> guiManager) {
        return new PaginatedGUI.Builder<>(plugin, guiManager, new ArrayList<>());
    }

    @Override
    public @Unmodifiable Set<Player> getViewers() {
        return this.getCurrentGUI().getViewers();
    }

    public void removeViewer(Player player) {
        super.removeViewer(player);
        this.pages.forEach(page -> page.removeViewer(player));
    }

    @Unmodifiable
    public List<GUI<P>> getPages() {
        return Collections.unmodifiableList(this.pages);
    }

    public static class Builder<P extends JavaPlugin> extends GUI.Builder<PaginatedGUI.Builder<P>, PaginatedGUI<P>, P> {

        private final P plugin;
        private final GUIManager<P> guiManager;
        private final List<GUI<P>> pages;

        private Builder(P plugin, GUIManager<P> guiManager, List<GUI<P>> pages) {
            this.plugin = plugin;
            this.guiManager = guiManager;
            this.pages = pages;
        }

        public PaginatedGUI.Builder<P> addPage(GUI<P> page) {
            this.pages.add(page);
            return this;
        }

        public PaginatedGUI.Builder<P> addPages(List<GUI<P>> pages) {
            this.pages.addAll(pages);
            return this;
        }

        public PaginatedGUI.Builder<P> pagePatterns(List<Pattern<P>> patterns) {
            this.pages.forEach(page -> page.addPatterns(patterns));
            return this;
        }

        @Override
        public PaginatedGUI<P> build() {
            return new PaginatedGUI<>(
                    this.plugin,
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
