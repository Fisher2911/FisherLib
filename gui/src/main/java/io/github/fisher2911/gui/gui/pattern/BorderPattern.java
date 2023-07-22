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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BorderPattern implements Pattern {

    private final List<GUIItem> borders;
    private final MetadataKey<? extends BorderPattern> key;
    private final Metadata metadata;
    private final int priority;

    protected BorderPattern(List<GUIItem> borders, MetadataKey<? extends BorderPattern> key, int priority) {
        this.borders = borders;
        this.key = key;
        this.metadata = Metadata.mutableEmpty();
        this.priority = priority;
    }

    @Override
    public @NotNull Metadata getMetadata() {
        return this.metadata;
    }

    public static int getSlotsPerRow(GUI gui) {
        return switch (gui.getType()) {
            case CHEST -> 9;
            case DROPPER -> 3;
            default -> -1;
        };
    }

    @Override
    public void apply(GUI gui) {
        if (this.borders.isEmpty()) return;
        final int slotsPerRow = getSlotsPerRow(gui);
        if (slotsPerRow == -1) return;
        final int size = gui.getInventorySize();
        final int rows = size / slotsPerRow;
        int rowIndex = 0;
        int leftColIndex = 0;
        int rightColIndex = 0;
        for (int i = 0; i < rows; i++) {
            if (i == 0) {
                for (int j = 0; j < slotsPerRow; j++) {
                    gui.replaceItem(
                            GUISlot.of(i + j),
                            this.borders.get(rowIndex),
                            item -> Pattern.replacePredicate(item, this)
                    );
                    rowIndex = getNextIndex(rowIndex);
                }
                leftColIndex = getNextIndex(0);
                rightColIndex = rowIndex;
                continue;
            }
            gui.replaceItem(
                    GUISlot.of((i + 1) * slotsPerRow - 1),
                    this.borders.get(rightColIndex),
                    item -> Pattern.replacePredicate(item, this)
            );
            rightColIndex = getNextIndex(rightColIndex);

            gui.replaceItem(
                    GUISlot.of(i * slotsPerRow),
                    this.borders.get(leftColIndex),
                    item -> Pattern.replacePredicate(item, this)
            );
            leftColIndex = getNextIndex(leftColIndex);

            if (i == rows - 1) {
                rowIndex = leftColIndex;
                for (int j = 1; j < slotsPerRow - 1; j++) {
                    gui.replaceItem(
                            GUISlot.of(i * slotsPerRow + j),
                            this.borders.get(rowIndex),
                            item -> Pattern.replacePredicate(item, this)
                    );
                    rowIndex = getNextIndex(rowIndex);
                }
            }
        }
    }

    private int getNextIndex(int i) {
        if (i == this.borders.size() - 1) {
            return 0;
        }
        return i + 1;
    }

    @Override
    public MetadataKey<? extends BorderPattern> getKey() {
        return this.key;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

}
