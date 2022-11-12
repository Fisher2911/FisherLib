/*
 *     Kingdoms Plugin
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

package io.github.fisher2911.fisherlib.config.condition;

import io.github.fisher2911.fisherlib.FishPlugin;
import io.github.fisher2911.fisherlib.config.condition.impl.PlaceholderConditionals;
import io.github.fisher2911.fisherlib.config.serializer.GuiItemSerializer;
import io.github.fisher2911.fisherlib.gui.ConditionalItem;
import io.github.fisher2911.fisherlib.util.function.TriFunction;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConditionSerializer {

    private static final String REQUIRED_METADATA_PATH = "required-metadata";
    private static final String CONDITIONS_PATH = "conditions";
    private static final String ITEM_PATH = "item";

    // base, list node
    private static final Map<String, TriFunction<FishPlugin<?>, ConfigurationNode, ConfigurationNode, List<MetadataPredicate>>> loaders = new HashMap<>();

    public static void registerLoader(String type, TriFunction<FishPlugin<?>, ConfigurationNode, ConfigurationNode, List<MetadataPredicate>> loader) {
        loaders.put(type, loader);
    }

    static {
        registerLoader(ConditionType.PARSE_PLACEHOLDERS, ConditionSerializer::loadPlaceholderConditions);
    }

    public static ItemConditions loadConditional(FishPlugin<?> plugin, ConfigurationNode source) throws SerializationException {
        final ConditionalItem item = GuiItemSerializer.INSTANCE.deserialize(plugin, source);
        final List<MetadataPredicate> conditionalList = new ArrayList<>();
        for (var entry : source.node(CONDITIONS_PATH).childrenMap().entrySet()) {
            if (!(entry.getKey() instanceof final String type)) continue;
            final var loader = loaders.get(type);
            if (loader == null) {
                throw new SerializationException("No loader for condition type: " + entry.getKey());
            }
            final List<MetadataPredicate> conditionals = loader.apply(plugin, source, entry.getValue());
            conditionalList.addAll(conditionals);
        }
        return new ItemConditions(conditionalList, item);
    }

    private static List<MetadataPredicate> loadPlaceholderConditions(FishPlugin<?> plugin, ConfigurationNode base, ConfigurationNode source) {
        try {
            return source.getList(String.class, new ArrayList<>())
                    .stream()
                    .map(s -> PlaceholderConditionals.parse(plugin, s)).
                    collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Could not load placeholder conditionals", e);
        }
    }


}
