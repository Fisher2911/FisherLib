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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * This pattern is used to add previous and next page buttons to a {@link PaginatedGUI}.
 */
public class PaginatedPattern implements Pattern {

    private final @Nullable GUIItem previousPageItem;
    private final @Nullable GUIItem nextPageItem;
    private final Function<GUI, GUISlot> previousPageItemSlotFunction;
    private final Function<GUI, GUISlot> nextPageItemSlotFunction;
    private final Metadata metadata;
    private final MetadataKey<? extends PaginatedPattern> key;
    private final int priority;

    protected PaginatedPattern(
            @Nullable GUIItem previousPageItem,
            @Nullable GUIItem nextPageItem,
            Function<GUI, GUISlot> previousPageItemSlotFunction,
            Function<GUI, GUISlot> nextPageItemSlotFunction,
            MetadataKey<? extends PaginatedPattern> key,
            int priority
    ) {
        this.previousPageItem = previousPageItem;
        this.nextPageItem = nextPageItem;
        if (this.previousPageItem != null) {
            this.previousPageItem.appendListener(GUIClickEvent.class, e -> {
                final PaginatedGUI gui = e.getGUI().getOwner();
                if (gui == null) return;
                gui.previousPage();
            });
        }
        if (this.nextPageItem != null) {
            this.nextPageItem.appendListener(GUIClickEvent.class, e -> {
                final PaginatedGUI gui = e.getGUI().getOwner();
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
            @Nullable GUIItem previousPageItem,
            @Nullable GUIItem nextPageItem,
            MetadataKey<? extends PaginatedPattern> key,
            int priority
    ) {
        this(
                previousPageItem,
                nextPageItem,
                GUI::getDefaultPaginatedPreviousPageSlot,
                GUI::getDefaultPaginatedNextPageSlot,
                key,
                priority
        );
    }


    @Override
    public @NotNull Metadata getMetadata() {
        return this.metadata;
    }

    @Override
    public void apply(GUI gui) {
        final PaginatedGUI owner = gui.getOwner();
        if (owner == null) {
            this.applyNonPaginatedGUI(gui);
            return;
        }
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

    private void applyNonPaginatedGUI(GUI gui) {
        for (int guiPage = 0; guiPage < gui.getPageSize(); guiPage++) {
            if (guiPage > 0 && this.previousPageItem != null) {
                gui.replaceItem(
                        guiPage,
                        this.previousPageItemSlotFunction.apply(gui),
                        this.previousPageItem,
                        item -> Pattern.replacePredicate(item, this)
                );
            }
            if (guiPage < gui.getPageSize() - 1) {
                if (this.nextPageItem != null) {
                    gui.replaceItem(
                            guiPage,
                            this.nextPageItemSlotFunction.apply(gui),
                            this.nextPageItem,
                            item -> Pattern.replacePredicate(item, this)
                    );
                }
            }
        }
    }

    @Override
    public MetadataKey<? extends PaginatedPattern> getKey() {
        return this.key;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

}
