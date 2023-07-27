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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class BuiltInMetadata {

    private static final Map<String, MetadataKey<?>> metadata = new HashMap<>();

    protected BuiltInMetadata() {}

    private static final JavaPlugin PLUGIN = JavaPlugin.getProvidingPlugin(BuiltInMetadata.class);

    public static final MetadataKey<Object[]> PLACEHOLDERS_KEY = create("placeholders", Object[].class);
    public static final MetadataKey<Player> PLAYER_KEY = create("player", Player.class);

    public static <V> MetadataKey<V> create(String key, Class<V> type) {
        final MetadataKey<V> metadataKey = MetadataKey.of(key(key), type);
        metadata.put(key, metadataKey);
        return metadataKey;
    }

    protected static NamespacedKey key(String key) {
        return new NamespacedKey(PLUGIN, key);
    }

    public static <V> MetadataKey<V> getKey(String name) {
        return (MetadataKey<V>) metadata.get(name);
    }

}
