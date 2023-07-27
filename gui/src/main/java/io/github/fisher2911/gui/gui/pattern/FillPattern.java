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

import java.util.Collection;
import java.util.function.Predicate;

/**
 * This pattern is used to fill a GUI with items.
 */
public class FillPattern implements Pattern {

    private final MetadataKey<? extends FillPattern> key;
    private final Metadata metadata;
    private final Collection<GUIItem> guiItems;
    private final Predicate<GUISlot> slotFillPredicate;
    private final Predicate<GUIItem> itemFillPredicate;
    private final int priority;

    protected FillPattern(
            Collection<GUIItem> guiItems,
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
        int nextSlot = 0;
        int guiPage = 0;
        for (final GUIItem guiItem : this.guiItems) {
            if (!this.itemFillPredicate.test(guiItem)) {
                continue;
            }
            final ApplySlotResult result = this.applySlot(nextSlot, guiPage,  gui, guiItem);
            nextSlot = result.nextSlot();
            guiPage = result.guiPage();
            if (nextSlot == -1 || guiPage == -1) break;
        }
    }

    private void applyPaginated(PaginatedGUI gui) {
        int nextSlot = 0;
        final GUI currentGUI = gui.getCurrentGUI();
        for (final GUIItem guiItem : this.guiItems) {
            final GUI itemGUI = guiItem.getGUI();
            if (itemGUI != null && !currentGUI.equals(itemGUI)) {
                continue;
            }
            if (!this.itemFillPredicate.test(guiItem)) {
                continue;
            }
            nextSlot = this.applySlot(nextSlot, 0, gui, guiItem).nextSlot;
            if (nextSlot == -1) break;
        }
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
