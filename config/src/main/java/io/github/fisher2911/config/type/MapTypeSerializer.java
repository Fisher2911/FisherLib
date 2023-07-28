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

package io.github.fisher2911.config.type;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapTypeSerializer<T> implements TypeSerializer<Map<String, T>> {

    private final TypeSerializer<T> serializer;

    private MapTypeSerializer(TypeSerializer<T> serializer) {
        this.serializer = serializer;
    }

    public static <T> MapTypeSerializer<T> create(TypeSerializer<T> serializer) {
        return new MapTypeSerializer<>(serializer);
    }

    @Override
    public @Nullable Map<String, T> load(ConfigurationSection section, String path) {
        final Map<String, T> map = new HashMap<>();
        final ConfigurationSection mapSection = section.getConfigurationSection(path);
        if (mapSection == null) {
            return new HashMap<>();
        }
        for (final String key : mapSection.getKeys(false)) {
            final T value = this.serializer.load(mapSection, key);
            if (value != null) {
                map.put(key, value);
            }
        }
        return map;
    }

    public @Nullable <K> Map<K, T> load(ConfigurationSection section, String path, Function<String, K> function) {
        return Objects.requireNonNullElse(this.load(section, path), Collections.<String, T>emptyMap())
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> function.apply(e.getKey()), Map.Entry::getValue));
    }

    @Override
    public @NotNull List<Map<String, T>> loadList(ConfigurationSection section, String path) {
        final List<Map<String, T>> list = new ArrayList<>();
        for (final String key : section.getKeys(false)) {
            final Map<String, T> map = this.load(section, key);
            if (map != null) {
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public void save(ConfigurationSection section, String path, Map<String, T> value) {
        for (final Map.Entry<String, T> entry : value.entrySet()) {
            this.serializer.save(section, entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<Map<String, T>> value) {
        for (int i = 0; i < value.size(); i++) {
            this.save(section, path + "." + i, value.get(i));
        }
    }

}
