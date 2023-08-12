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
import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.gui.gui.GUISlot;
import io.github.fisher2911.gui.gui.PaginatedGUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * This pattern is used to fill a GUI with items.
 */
public class FillPattern implements Pattern {

    private final MetadataKey<? extends FillPattern> key;
    private final Metadata metadata;
    private final Supplier<Stream<GUIItem>> guiItems;
    private final Predicate<GUISlot> slotFillPredicate;
    private final Predicate<GUIItem> itemFillPredicate;
    private final int priority;

    protected FillPattern(
            Supplier<Stream<GUIItem>> guiItems,
            Predicate<GUISlot> slotFillPredicate,
            Predicate<GUIItem> itemFillPredicate,
            MetadataKey<? extends FillPattern> key,
            int priority
    ) {
        this.metadata = Metadata.mutableEmpty();
        this.guiItems = guiItems;
        this.slotFillPredicate = slotFillPredicate;
        this.itemFillPredicate = itemFillPredicate;
        this.key = key;
        this.priority = priority;
    }

    @Override
    public @NotNull Metadata getMetadata() {
        return this.metadata;
    }

    @Override
    public void apply(GUI gui) {
        if (gui instanceof final PaginatedGUI paginatedGUI) {
            this.applyPaginated(paginatedGUI);
            return;
        }
        final AtomicInteger nextSlot = new AtomicInteger(0);
        final AtomicInteger guiPage = new AtomicInteger(0);
        this.guiItems.get().takeWhile(guiItem -> {
            if (guiItem == null) return false;
            if (!this.itemFillPredicate.test(guiItem)) return true;
            final ApplySlotResult result = this.applySlot(nextSlot.get(), guiPage.get(),  gui, guiItem);
            nextSlot.set(result.nextSlot());
            guiPage.set(result.guiPage());
            return nextSlot.get() != -1 && guiPage.get() != -1;
        }).anyMatch(i -> false);
    }

    private void applyPaginated(PaginatedGUI gui) {
        final GUI currentGUI = gui.getCurrentGUI();
        final AtomicInteger nextSlot = new AtomicInteger(0);
        this.guiItems.get().takeWhile(guiItem -> {
            if (guiItem == null) return false;
            final GUI itemGUI = guiItem.getGUI();
            if (itemGUI != null && !currentGUI.equals(itemGUI)) {
                return true;
            }
            if (!this.itemFillPredicate.test(guiItem)) {
                return true;
            }
            nextSlot.set(this.applySlot(nextSlot.get(), 0, gui, guiItem).nextSlot);
            if (nextSlot.get() == -1) return false;
            return false;
        }).anyMatch(i -> true);
    }

    private ApplySlotResult applySlot(int nextSlot, int guiPage, GUI gui, GUIItem guiItem) {
        @Nullable ApplySlotResult applySlotResult = this.attemptApplySlot(nextSlot, gui, guiItem, guiPage);
        while (applySlotResult != null && !applySlotResult.replaceSuccessful()) {
            applySlotResult = this.attemptApplySlot(applySlotResult.nextSlot(), gui, guiItem, applySlotResult.guiPage());
        }
        if (applySlotResult == null) {
            return new ApplySlotResult(-1, -1, false);
        }
        return applySlotResult;
    }

    private @Nullable ApplySlotResult attemptApplySlot(int nextSlot, GUI gui, GUIItem guiItem, int guiPage) {
        GUISlot slot = GUISlot.of(nextSlot);
        int inventorySize = gui.getInventorySize();
        while (!this.slotFillPredicate.test(slot) && nextSlot < inventorySize) {
            nextSlot++;
            slot = GUISlot.of(nextSlot);
        }
        if (nextSlot >= inventorySize && (gui.isExpandable() || guiPage < gui.getPageSize() - 1)) {
            return new ApplySlotResult(0, guiPage + 1, false); // attemptApplySlot(0, gui, guiItem, guiPage + 1);
        }
        if (nextSlot >= inventorySize) {
            return null;
        }
        if (!gui.replaceItem(
                guiPage,
                slot,
                guiItem,
                item -> Pattern.replacePredicate(item, this)
        )) {
            return new ApplySlotResult(nextSlot + 1, guiPage, false);
        }
        return new ApplySlotResult(nextSlot + 1, guiPage, true);
    }

    private static record ApplySlotResult(int nextSlot, int guiPage, boolean replaceSuccessful) {

    }

    @Override
    public MetadataKey<? extends FillPattern> getKey() {
        return this.key;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

}
