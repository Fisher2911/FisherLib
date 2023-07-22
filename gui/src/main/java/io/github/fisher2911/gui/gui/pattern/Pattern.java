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

/**
 * You can apply patterns to GUI's in order to distribute GUIItems in a specific way.
 * The built-in patterns are {@link BorderPattern}, {@link FillPattern}, and {@link PaginatedPattern}.
 */
@SuppressWarnings("unused")
public interface Pattern extends Metadatable, Comparable<Pattern> {

    String PATTERN_KEY_NAME = "pattern";

    /**
     *
     * @param clazz The class of the pattern
     * @param <V> - The type of the pattern
     * @return The MetadataKey for the pattern
     */
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

    /**
     * This replaces GUIItems that have the same pattern key as the pattern being checked, or if they do not exist
     * @param item - the item to be compared against
     * @param pattern - the pattern being checked
     * @return true if the pattern should replace the previous GUIItem
     */
    static boolean replacePredicate(@Nullable GUIItem item, Pattern pattern) {
        return item == null || Objects.equals(item.getMetadata().get(pattern.getKey()), pattern);
    }

    /**
     * This applies the pattern to the GUI
     * @param gui - the GUI to apply the pattern to
     */
    void apply(GUI gui);

    /**
     *
     * @return the {@link MetadataKey} for the pattern, used to identify GUIItem's that
     * the pattern applies to
     */
    MetadataKey<? extends Pattern> getKey();

    // lower priority is applied first

    /**
     * Lower priorities are applied first, so if you want a pattern to be applied first, give it a lower priority
     * This means that other patterns will not overwrite them unless they explicitly do so in their replace predicate.
     * The built-in patterns do not overwrite other patterns.
     * @return the priority of the pattern
     */
    int getPriority();

    /**
     *
     * @param o - the pattern to compare to
     * @return see {@link Integer#compare(int, int)}
     */
    @Override
    default int compareTo(@NotNull Pattern o) {
        return Integer.compare(getPriority(), o.getPriority());
    }

}
