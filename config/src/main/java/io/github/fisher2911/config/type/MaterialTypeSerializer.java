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

import io.github.fisher2911.common.util.EnumUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class MaterialTypeSerializer implements TypeSerializer<Material> {

    public static final MaterialTypeSerializer INSTANCE = new MaterialTypeSerializer();

    private MaterialTypeSerializer() {
    }

    @Override
    public @Nullable Material load(ConfigurationSection section, String path) {
        return EnumUtils.tryParseEnum(
                StringTypeSerializer.INSTANCE.load(section, path),
                Material.class
        );
    }

    @Override
    public @NotNull List<Material> loadList(ConfigurationSection section, String path) {
        return section.getStringList(path)
                .stream()
                .map(s -> EnumUtils.tryParseEnum(s, Material.class))
                .collect(Collectors.toList());
    }

    @Override
    public void save(ConfigurationSection section, String path, Material value) {
        section.set(path, value.toString());
    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<Material> value) {
        section.set(path, value.stream().map(Material::toString).collect(Collectors.toList()));
    }

}
