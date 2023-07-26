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

public class DoubleTypeSerializer implements TypeSerializer<Double> {

    public static final DoubleTypeSerializer INSTANCE = new DoubleTypeSerializer();

    private DoubleTypeSerializer() {}

    @Override
    public @Nullable Double load(ConfigurationSection section, String path) {
        return section.getDouble(path);
    }

    @Override
    public @NotNull List<Double> loadList(ConfigurationSection section, String path) {
        return section.getDoubleList(path);
    }

    @Override
    public void save(ConfigurationSection section, String path, Double value) {
        section.set(path, value);
    }

}
