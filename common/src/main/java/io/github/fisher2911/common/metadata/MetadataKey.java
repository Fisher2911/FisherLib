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

public record MetadataKey<V>(NamespacedKey key, Class<V> valueType) {

//    public static final MetadataKey<Object> OBJECT_KEY = new MetadataKey<>("object", Object.class);
    private static final String OBJECT_KEY_NAME = "object";

    public static MetadataKey<Object> objectKey(JavaPlugin plugin) {
        return new MetadataKey<>(new NamespacedKey(plugin, OBJECT_KEY_NAME), Object.class);
    }

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

}
