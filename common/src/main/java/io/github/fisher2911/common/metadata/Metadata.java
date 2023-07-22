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

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * This represents a set of metadata that can be attached to any object,
 * similar to {@link org.bukkit.persistence.PersistentDataContainer}.
 * To get an object from metadata, you must use an {@link MetadataKey} which defines
 * the return type of the object. If the return type is unknown, you can use
 * {@link MetadataKey#objectKey(JavaPlugin)}.
 */
@SuppressWarnings({"unused"})
public class Metadata {

    private final Map<MetadataKey<?>, Object> metadata;

    public Metadata(final Map<MetadataKey<?>, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * @return the underlying map of metadata, changes to the map
     * will reflect the underlying map
     */
    public Map<MetadataKey<?>, Object> get() {
        return this.metadata;
    }

    /**
     * @param metadata - the map of metadata to create the Metadata object from
     * @return a new Metadata object
     */
    public static Metadata of(Map<MetadataKey<?>, Object> metadata) {
        return new Metadata(metadata);
    }

    /**
     * @return a new immutable empty Metadata object
     */
    public static Metadata empty() {
        return new Metadata(Map.of());
    }

    /**
     * @return a new mutable empty Metadata object
     */
    public static Metadata mutableEmpty() {
        return new Metadata(new HashMap<>());
    }

    /**
     * @return a copy of this Metadata object
     */
    public Metadata copy() {
        return new Metadata(new HashMap<>(this.metadata));
    }

    /**
     * @param metadata  - the metadata to copy
     * @param overwrite - whether to overwrite existing keys with the new metadata
     * @return a copy of this Metadata object with the new metadata
     */
    public Metadata copyWith(Metadata metadata, boolean overwrite) {
        final Map<MetadataKey<?>, Object> newMap = new HashMap<>(this.metadata);
        if (overwrite) {
            newMap.putAll(metadata.get());
        } else {
            metadata.get().forEach((key, value) -> {
                if (!newMap.containsKey(key)) {
                    newMap.put(key, value);
                }
            });
        }
        return new Metadata(newMap);
    }

    /**
     * @param key - the key to get the value of
     * @param <T> - the type of the value
     * @return the value of the key, or null if the key does not exist or the value is not of the correct type
     */
    public @Nullable <T> T get(MetadataKey<T> key) {
        final Object o = this.metadata.get(key);
        if (o == null) return null;
        if (!key.valueType().isInstance(o)) return null;
        return key.valueType().cast(o);
    }

    /**
     * @param key   - the key to get the value of
     * @param value - the value to set the key to
     * @param <T>   - the type of the value
     */
    public <T> void set(MetadataKey<T> key, T value) {
        this.metadata.put(key, value);
    }

    /**
     * @param key       - the key to get the value of
     * @param value     - the value to set the key to
     * @param overwrite - whether to overwrite the value if the key already exists
     */
    public void set(MetadataKey<?> key, Object value, boolean overwrite) {
        if (overwrite) {
            this.metadata.put(key, value);
            return;
        }
        this.metadata.putIfAbsent(key, value);
    }

    /**
     * @param metadata - the metadata to set, this clears the preexisting metadata
     */
    public void set(Map<MetadataKey<?>, Object> metadata) {
        this.metadata.clear();
        this.metadata.putAll(metadata);
    }

    /**
     * This behaves similarly to {@link Metadata#copyWith(Metadata, boolean)}
     *
     * @param metadata  - the metadata to set
     * @param overwrite - whether to overwrite existing keys with the new metadata
     */
    public void putAll(Map<MetadataKey<?>, Object> metadata, boolean overwrite) {
        if (overwrite) {
            this.metadata.putAll(metadata);
        } else {
            metadata.forEach((key, value) -> {
                if (!this.metadata.containsKey(key)) {
                    this.metadata.put(key, value);
                }
            });
        }
    }

    /**
     * @param metadata  - the metadata to set
     * @param overwrite - whether to overwrite existing keys with the new metadata
     */
    public void putAll(Metadata metadata, boolean overwrite) {
        this.putAll(metadata.get(), overwrite);
    }

    /**
     * @param key - the key to remove
     * @param <T> - the type of the value
     * @return the value of the key, or null if the key does not exist or the value is not of the correct type
     */
    public <T> T remove(MetadataKey<T> key) {
        final Object o = this.metadata.remove(key);
        if (o == null) return null;
        if (!key.valueType().isInstance(o)) return null;
        return key.valueType().cast(o);
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "metadata=" + metadata +
                '}';
    }

}
