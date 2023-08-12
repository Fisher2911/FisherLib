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

import io.github.fisher2911.common.item.ItemBuilder;
import io.github.fisher2911.common.util.EnumUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemBuilderTypeSerializer implements TypeSerializer<ItemBuilder> {

    public static final ItemBuilderTypeSerializer INSTANCE = new ItemBuilderTypeSerializer();

    private ItemBuilderTypeSerializer() {
    }

    private static final String MATERIAL_PATH = "material";
    private static final String AMOUNT_PATH = "amount";
    private static final String NAME_PATH = "name";
    private static final String LORE_PATH = "lore";
    private static final String ENCHANTMENTS_PATH = "enchantments";
    private static final String FLAGS_PATH = "flags";

    @Override
    public @Nullable ItemBuilder load(ConfigurationSection parent, String path) {
        final ConfigurationSection section = parent.getConfigurationSection(path);
        if (section == null) return null;
        final Material material = MaterialTypeSerializer.INSTANCE.load(section, MATERIAL_PATH);
        if (material == null) throw new IllegalArgumentException("Material cannot be null");
        final ItemBuilder builder = ItemBuilder.from(material)
                .name(StringTypeSerializer.INSTANCE.load(section, NAME_PATH))
                .lore(StringTypeSerializer.INSTANCE.loadList(section, LORE_PATH))
                .amount(Math.max(1, IntTypeSerializer.INSTANCE.load(section, AMOUNT_PATH)));
        final Map<Enchantment, Integer> enchantments = MapTypeSerializer.create(IntTypeSerializer.INSTANCE)
                .load(section, ENCHANTMENTS_PATH, s -> Registry.ENCHANTMENT.get(NamespacedKey.minecraft(s)));
        builder.enchantments(enchantments);
        builder.flag(
                StringTypeSerializer.INSTANCE.loadList(section, FLAGS_PATH)
                        .stream()
                        .map(s -> EnumUtils.tryParseEnum(s, ItemFlag.class))
                        .filter(f -> f != null)
                        .toArray(ItemFlag[]::new)
        );
        return builder;
    }

    @Override
    public @NotNull List<ItemBuilder> loadList(ConfigurationSection parent, String path) {
        final List<ItemBuilder> builders = new ArrayList<>();
        final ConfigurationSection section = parent.getConfigurationSection(path);
        if (section == null) return builders;
        for (final String key : section.getKeys(false)) {
            builders.add(load(section, key));
        }
        return builders;
    }

    @Override
    public void save(ConfigurationSection parent, String path, ItemBuilder value) {
        ConfigurationSection section = parent.getConfigurationSection(path);
        if (section == null) {
            section = parent.createSection(path);
        }
        final ItemMeta itemMeta = value.getItemMeta();
        MaterialTypeSerializer.INSTANCE.save(section, MATERIAL_PATH, value.getMaterial());
        IntTypeSerializer.INSTANCE.save(section, AMOUNT_PATH, value.getAmount());
        if (itemMeta == null) return;
        StringTypeSerializer.INSTANCE.save(section, NAME_PATH, itemMeta.getDisplayName());
        StringTypeSerializer.INSTANCE.saveList(section, LORE_PATH, itemMeta.getLore());
        MapTypeSerializer.create(IntTypeSerializer.INSTANCE)
                .save(section, ENCHANTMENTS_PATH, itemMeta.getEnchants().entrySet()
                        .stream()
                        .map(e -> Map.entry(e.getKey().getKey().getKey(), e.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                );
        StringTypeSerializer.INSTANCE.saveList(section, FLAGS_PATH, itemMeta.getItemFlags()
                .stream()
                .map(ItemFlag::toString)
                .collect(Collectors.toList()
                ));
    }

    @Override
    public void saveList(ConfigurationSection parent, String path, List<ItemBuilder> value) {
        ConfigurationSection section = parent.getConfigurationSection(path);
        if (section == null) {
            section = parent.createSection(path);
        }
        for (int i = 0; i < value.size(); i++) {
            save(section, String.valueOf(i), value.get(i));
        }
    }

}
