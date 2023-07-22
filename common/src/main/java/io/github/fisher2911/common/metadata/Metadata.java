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

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Metadata {

    private final Map<MetadataKey<?>, Object> metadata;

    public Metadata(final Map<MetadataKey<?>, Object> metadata) {
        this.metadata = metadata;
    }

    public Map<MetadataKey<?>, Object> get() {
        return this.metadata;
    }

    public static Metadata of(Map<MetadataKey<?>, Object> metadata) {
        return new Metadata(metadata);
    }

    public static Metadata empty() {
        return new Metadata(Map.of());
    }

    public static Metadata mutableEmpty() {
        return new Metadata(new HashMap<>());
    }

    public Metadata copy() {
        return new Metadata(new HashMap<>(this.metadata));
    }

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

    @Nullable
    public <T> T get(MetadataKey<T> key) {
        final Object o = this.metadata.get(key);
        if (o == null) return null;
        if (!key.valueType().isInstance(o)) return null;
        return key.valueType().cast(o);
    }

    public <T> void set(MetadataKey<T> key, T value) {
        this.metadata.put(key, value);
    }

    public void set(MetadataKey<?> key, Object value, boolean overwrite) {
        if (overwrite) {
            this.metadata.put(key, value);
            return;
        }
        this.metadata.putIfAbsent(key, value);
    }

    public void set(Map<MetadataKey<?>, Object> metadata) {
        this.metadata.clear();
        this.metadata.putAll(metadata);
    }

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

    public void putAll(Metadata metadata, boolean overwrite) {
        this.putAll(metadata.get(), overwrite);
    }

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
