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

package io.github.fisher2911.fisherlib.config.condition.impl;

import io.github.fisher2911.fisherlib.FishPlugin;
import io.github.fisher2911.fisherlib.config.condition.ConditionOperation;
import io.github.fisher2911.fisherlib.config.condition.MetadataPredicate;
import io.github.fisher2911.fisherlib.gui.GuiKey;
import io.github.fisher2911.fisherlib.util.Metadata;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.function.Function;

public class PlaceholderConditionals implements MetadataPredicate {

    private final FishPlugin<?, ?> plugin;
    private final String toParse;
    private final ConditionOperation operation;
    private final String value;

    public PlaceholderConditionals(FishPlugin<?, ?> plugin, String toParse, ConditionOperation operation, String value) {
        this.plugin = plugin;
        this.toParse = toParse;
        this.operation = operation;
        this.value = value;
    }

    public boolean test(Metadata possible) {
        final Object[] placeholders = GuiKey.toPlaceholders(possible).toArray();
        final String parsed = this.plugin.getPlaceholders().apply(
                this.toParse,
                placeholders
        );
        final String parsedValue = this.plugin.getPlaceholders().apply(
                this.value,
                placeholders
        );
        return this.operation.test(parsed, parsedValue);
    }

    public String getToParse() {
        return toParse;
    }

    public ConditionOperation getOperation() {
        return operation;
    }

    public String getValue() {
        return value;
    }

    private static final Map<Character, Function<Character, ConditionOperation>> OPERATIONS = Map.of(
            '=', o -> (o == '=') ? ConditionOperation.EQUALS : null,
            '!', o -> (o == '=') ? ConditionOperation.NOT_EQUALS : null,
            '>', o -> (o == '=') ? ConditionOperation.GREATER_THAN_OR_EQUALS : ConditionOperation.GREATER_THAN,
            '<', o -> (o == '=') ? ConditionOperation.LESS_THAN_OR_EQUALS : ConditionOperation.LESS_THAN
    );

    public static PlaceholderConditionals parse(FishPlugin<?, ?> plugin, String string) {
        final char[] chars = string.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            final char previous = i > 0 ? chars[i - 1] : ' ';
            if (previous == '\\') continue;
            final char c = chars[i];
            final Function<Character, ConditionOperation> function = OPERATIONS.get(c);
            if (function != null) {
                char next = (i + 1 < chars.length) ? chars[i + 1] : ' ';
                final ConditionOperation operation = function.apply(next);
                if (operation != null) {
                    final String toParse = string.substring(0, i);
                    final String value = string.substring(i + (next == '=' ? 2 : 1));
                    return new PlaceholderConditionals(plugin, toParse, operation, value);
                }
            }
        }
        return null;
    }

}
