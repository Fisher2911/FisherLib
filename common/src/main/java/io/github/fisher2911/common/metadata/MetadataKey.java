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

package io.github.fisher2911.common.metadata;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * Represents a key that can be used to get an object from {@link Metadata}.
 * @param <V> - the type of object that this key represents
 */
public record MetadataKey<V>(NamespacedKey key, Class<V> valueType) {

    private static final String OBJECT_KEY_NAME = "object";

    /**
     * Creates a new {@link MetadataKey} with the given {@link NamespacedKey} and {@link java.lang.Object} type.
     * @param plugin - the plugin that this key belongs to
     * @return a new {@link MetadataKey} with the given {@link NamespacedKey} and {@link java.lang.Object} type.
     */
    public static MetadataKey<Object> objectKey(JavaPlugin plugin) {
        return new MetadataKey<>(new NamespacedKey(plugin, OBJECT_KEY_NAME), Object.class);
    }

    /**
     * Creates a new {@link MetadataKey} with the given {@link NamespacedKey} and {@link V} type.
     * @param key - the {@link NamespacedKey} to use
     * @return a new {@link MetadataKey} with the given {@link NamespacedKey} and {@link V} type.
     */
    public static <V> MetadataKey<V> of(NamespacedKey key, Class<V> valueType) {
        return new MetadataKey<>(key, valueType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MetadataKey<?> that = (MetadataKey<?>) o;
        return Objects.equals(this.key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key);
    }

    @Override
    public String toString() {
        return "MetadataKey{" +
                "key=" + key +
                ", valueType=" + valueType +
                '}';
    }

}
