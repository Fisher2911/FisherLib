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

import io.github.fisher2911.common.metadata.MetadataKey;
import io.github.fisher2911.common.metadata.Metadatable;
import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.gui.gui.GUISlot;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Pattern extends Metadatable, Comparable<Pattern> {

    String PATTERN_KEY_NAME = "pattern";

    static <V extends Pattern> MetadataKey<V> getPatternKey(Class<V> clazz) {
        return MetadataKey.of(new NamespacedKey(JavaPlugin.getProvidingPlugin(clazz), PATTERN_KEY_NAME), clazz);
    }

    static BorderPattern borderPattern(
            List<GUIItem> borders,
            int priority
    ) {
        return new BorderPattern(
                borders,
                getPatternKey(BorderPattern.class),
                priority
        );
    }

    static PaginatedPattern paginatedPattern(
            @Nullable GUIItem previousPageItem,
            @Nullable GUIItem nextPageItem,
            Function<GUI, GUISlot> previousPageItemSlotFunction,
            Function<GUI, GUISlot> nextPageItemSlotFunction,
            int priority
    ) {
        return new PaginatedPattern(
                previousPageItem,
                nextPageItem,
                previousPageItemSlotFunction,
                nextPageItemSlotFunction,
                getPatternKey(PaginatedPattern.class),
                priority
        );
    }

    static PaginatedPattern paginatedPattern(
            @Nullable GUIItem previousPageItem,
            @Nullable GUIItem nextPageItem,
            int priority
    ) {
        return new PaginatedPattern(
                previousPageItem,
                nextPageItem,
                getPatternKey(PaginatedPattern.class),
                priority
        );
    }

    static FillPattern fillPattern(
            Collection<GUIItem> guiItems,
            Predicate<GUISlot> slotFillPredicate,
            Predicate<GUIItem> itemFillPredicate,
            int priority
    ) {
        return new FillPattern(
                guiItems,
                slotFillPredicate,
                itemFillPredicate,
                getPatternKey(FillPattern.class),
                priority
        );
    }

    static boolean replacePredicate(GUIItem item, Pattern pattern) {
        return item == null || Objects.equals(item.getMetadata().get(pattern.getKey()), pattern);
    }

    void apply(GUI gui);

    MetadataKey<? extends Pattern> getKey();

    // lower priority is applied first
    int getPriority();

    @Override
    default int compareTo(@NotNull Pattern o) {
        return Integer.compare(getPriority(), o.getPriority());
    }

}
