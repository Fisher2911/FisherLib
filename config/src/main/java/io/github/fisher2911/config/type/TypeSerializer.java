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

import java.util.List;

public interface TypeSerializer<T> {

    List<TypeSerializer<?>> DEFAULTS = List.of(
            ByteTypeSerializer.INSTANCE,
            DoubleTypeSerializer.INSTANCE,
            FloatTypeSerializer.INSTANCE,
            IntTypeSerializer.INSTANCE,
            LongTypeSerializer.INSTANCE,
            StringTypeSerializer.INSTANCE,
            BooleanTypeSerializer.INSTANCE,
            MaterialTypeSerializer.INSTANCE,
            ItemBuilderTypeSerializer.INSTANCE
    );

    @Nullable T load(ConfigurationSection section, String path);

    @NotNull List<T> loadList(ConfigurationSection section, String path);

    void save(ConfigurationSection section, String path, T value);

    void saveList(ConfigurationSection section, String path, List<T> value);

}
