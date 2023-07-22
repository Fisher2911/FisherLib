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
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Predicate;

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
        for (final GUIItem guiItem : this.guiItems) {
            if (!this.itemFillPredicate.test(guiItem)) {
                continue;
            }
            nextSlot = this.applySlot(nextSlot, gui, guiItem);
            if (nextSlot == -1) break;
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
            nextSlot = this.applySlot(nextSlot, gui, guiItem);
            if (nextSlot == -1) break;
        }
    }

    private int applySlot(int nextSlot, GUI gui, GUIItem guiItem) {
        @Nullable ApplySlotResult applySlotResult = this.attemptApplySlot(nextSlot, gui, guiItem);
        while (applySlotResult != null && !applySlotResult.replaceSuccessful()) {
            applySlotResult = this.attemptApplySlot(applySlotResult.nextSlot(), gui, guiItem);
        }
        if (applySlotResult == null) return -1;
        return applySlotResult.nextSlot();
    }

    private @Nullable ApplySlotResult attemptApplySlot(int nextSlot, GUI gui, GUIItem guiItem) {
        GUISlot slot = GUISlot.of(nextSlot);
        int inventorySize = gui.getInventorySize();
        while (!this.slotFillPredicate.test(slot) && nextSlot < inventorySize) {
            nextSlot++;
            slot = GUISlot.of(nextSlot);
        }
        if (nextSlot >= inventorySize) return null;
        if (!gui.replaceItem(
                slot,
                guiItem,
                item -> Pattern.replacePredicate(item, this)
        )) {
            return new ApplySlotResult(nextSlot + 1, false);
        }
        return new ApplySlotResult(nextSlot + 1, true);
    }

    private static record ApplySlotResult(int nextSlot, boolean replaceSuccessful) { }

    @Override
    public MetadataKey<? extends FillPattern> getKey() {
        return this.key;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

}
