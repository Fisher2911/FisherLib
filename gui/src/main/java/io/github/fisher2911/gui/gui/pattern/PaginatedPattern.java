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

package io.github.fisher2911.gui.gui.pattern;

import io.github.fisher2911.common.metadata.Metadata;
import io.github.fisher2911.common.metadata.MetadataKey;
import io.github.fisher2911.gui.event.GUIClickEvent;
import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.gui.gui.GUISlot;
import io.github.fisher2911.gui.gui.PaginatedGUI;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class PaginatedPattern<P extends JavaPlugin> implements Pattern<P> {

    private final JavaPlugin plugin;
    private final @Nullable GUIItem<P> previousPageItem;
    private final @Nullable GUIItem<P> nextPageItem;
    private final Function<GUI<P>, GUISlot> previousPageItemSlotFunction;
    private final Function<GUI<P>, GUISlot> nextPageItemSlotFunction;
    private final Metadata metadata;
    private final MetadataKey<? extends PaginatedPattern<P>> key;
    private final int priority;

    protected PaginatedPattern(
            JavaPlugin plugin,
            @Nullable GUIItem<P> previousPageItem,
            @Nullable GUIItem<P> nextPageItem,
            Function<GUI<P>, GUISlot> previousPageItemSlotFunction,
            Function<GUI<P>, GUISlot> nextPageItemSlotFunction,
            MetadataKey<? extends PaginatedPattern<P>> key,
            int priority
    ) {
        this.plugin = plugin;
        this.previousPageItem = previousPageItem;
        this.nextPageItem = nextPageItem;
        if (this.previousPageItem != null) {
            this.previousPageItem.appendListener(GUIClickEvent.class, e -> {
                final PaginatedGUI<P> gui = e.getGUI().getOwner(this.plugin);
                if (gui == null) return;
                gui.previousPage();
            });
        }
        if (this.nextPageItem != null) {
            this.nextPageItem.appendListener(GUIClickEvent.class, e -> {
                final PaginatedGUI<P> gui = e.getGUI().getOwner(this.plugin);
                if (gui == null) return;
                gui.nextPage();
            });
        }
        this.previousPageItemSlotFunction = previousPageItemSlotFunction;
        this.nextPageItemSlotFunction = nextPageItemSlotFunction;
        this.key = key;
        this.metadata = Metadata.mutableEmpty();
        this.priority = priority;
    }

    protected PaginatedPattern(
            JavaPlugin plugin,
            @Nullable GUIItem<P> previousPageItem,
            @Nullable GUIItem<P> nextPageItem,
            MetadataKey<? extends PaginatedPattern<P>> key,
            int priority
    ) {
        this(
                plugin,
                previousPageItem,
                nextPageItem,
                GUI::getDefaultPaginatedPreviousPageSlot,
                GUI::getDefaultPaginatedNextPageSlot,
                key,
                priority
        );
    }


    @Override
    public @NotNull Metadata getMetaData() {
        return this.metadata;
    }

    @Override
    public void apply(GUI<P> gui) {
        final PaginatedGUI<P> owner = gui.getOwner(this.plugin);
        if (owner == null) return;
        final int index = owner.getPageIndex(gui);
        if (index > 0 && this.previousPageItem != null) {
            gui.replaceItem(
                    this.previousPageItemSlotFunction.apply(gui),
                    this.previousPageItem,
                    item -> Pattern.replacePredicate(item, this)
            );
        }
        if (index < owner.getPageSize() - 1) {
            if (this.nextPageItem != null) {
                gui.replaceItem(
                        this.nextPageItemSlotFunction.apply(gui),
                        this.nextPageItem,
                        item -> Pattern.replacePredicate(item, this)
                );
            }
        }
    }

    @Override
    public MetadataKey<? extends PaginatedPattern<P>> getKey() {
        return this.key;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

}
