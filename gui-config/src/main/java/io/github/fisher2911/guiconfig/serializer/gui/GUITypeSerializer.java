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

package io.github.fisher2911.guiconfig.serializer.gui;

import io.github.fisher2911.config.type.MapTypeSerializer;
import io.github.fisher2911.config.type.TypeSerializer;
import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.gui.gui.GUIManager;
import io.github.fisher2911.gui.gui.GUISlot;
import io.github.fisher2911.gui.gui.pattern.Pattern;
import io.github.fisher2911.gui.gui.type.ChestGUI;
import io.github.fisher2911.guiconfig.GUIItemManager;
import io.github.fisher2911.guiconfig.serializer.ClickActionTypeSerializer;
import io.github.fisher2911.guiconfig.serializer.GUIItemTypeSerializer;
import io.github.fisher2911.guiconfig.serializer.PatternSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GUITypeSerializer implements TypeSerializer<GUI.Builder<?, ?>> {

    private static GUITypeSerializer instance;

    public static GUITypeSerializer getInstance(
            GUIManager guiManager,
            GUIItemManager guiItemManager,
            GUIItemTypeSerializer itemTypeSerializer,
            ClickActionTypeSerializer actionSerializer,
            PatternSerializer patternSerializer
    ) {
        if (instance == null) {
            instance = new GUITypeSerializer(
                    guiManager,
                    guiItemManager,
                    itemTypeSerializer,
                    actionSerializer,
                    patternSerializer
            );
        }
        return instance;
    }

    private final GUIManager guiManager;
    private final GUIItemManager guiItemManager;
    private final GUIItemTypeSerializer itemTypeSerializer;
    private final ClickActionTypeSerializer actionSerializer;
    private final PatternSerializer patternSerializer;

    private GUITypeSerializer(
            GUIManager guiManager,
            GUIItemManager guiItemManager,
            GUIItemTypeSerializer itemTypeSerializer,
            ClickActionTypeSerializer actionSerializer,
            PatternSerializer patternSerializer
    ) {
        this.guiManager = guiManager;
        this.guiItemManager = guiItemManager;
        this.itemTypeSerializer = itemTypeSerializer;
        this.actionSerializer = actionSerializer;
        this.patternSerializer = patternSerializer;
    }

    private static final String TYPE_PATH = "type";
    private static final String TITLE_PATH = "title";
    private static final String ROWS_PATH = "size";
    private static final String EXPANDABLE_PATH = "expandable";
    private static final String PATTERNS_PATH = "patterns";
    private static final String PAGES_PATH = "pages";
    private static final String ITEMS_PATH = "items";

    @Override
    public @Nullable GUI.Builder<?, ?> load(ConfigurationSection section, String path) {
        final String type = section.getString(path + "." + TYPE_PATH);
        if (type == null) throw new IllegalStateException("GUI type not found at path " + path + "." + TYPE_PATH);
        final GUI.Builder<?, ?> builder = this.getBuilder(type);
        final String title = section.getString(path + "." + TITLE_PATH);
        if (title == null) throw new IllegalStateException("GUI title not found at path " + path + "." + TITLE_PATH);
        builder.title(title);
        final int rows = section.getInt(path + "." + ROWS_PATH);
        if (builder instanceof final ChestGUI.Builder chestGUIBuilder) {
            chestGUIBuilder.rows(rows);
        }
        final boolean expandable = section.getBoolean(path + "." + EXPANDABLE_PATH);
        builder.expandable(expandable);
        final ConfigurationSection patternsSection = section.getConfigurationSection(path + "." + PATTERNS_PATH);
        if (patternsSection != null) {
            final List<Pattern> patterns = new ArrayList<>();
            for (final String key : patternsSection.getKeys(false)) {
                patterns.add(this.patternSerializer.load(patternsSection, key));
            }
            builder.addPatterns(patterns);
        }
        final ConfigurationSection pagesSection = section.getConfigurationSection(path + "." + PAGES_PATH);
        if (pagesSection != null) {
            final List<Map<GUISlot, GUIItem>> pages = new ArrayList<>();
            for (final String key : pagesSection.getKeys(false)) {
                final ConfigurationSection pageSection = pagesSection.getConfigurationSection(key);
                if (pageSection == null) continue;
                final Map<GUISlot, GUIItem> items = MapTypeSerializer.create(GUIItemTypeSerializer.getInstance(this.actionSerializer))
                        .load(pageSection, ITEMS_PATH, s -> GUISlot.of(Integer.parseInt(s)))
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build()));
                pages.add(items);
            }
            for (int i = 0; i < pages.size(); i++) {
                builder.guiItems(pages.get(i));
                if (i < pages.size() - 1) {
                    builder.nextPage();
                }
            }
        } else {
            final Map<GUISlot, GUIItem> items = MapTypeSerializer.create(GUIItemTypeSerializer.getInstance(this.actionSerializer))
                    .load(section, ITEMS_PATH, s -> GUISlot.of(Integer.parseInt(s)))
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().build()));
            builder.guiItems(items);
        }
        return builder;
    }

    @Override
    public @NotNull List<GUI.Builder<?, ?>> loadList(ConfigurationSection section, String path) {
        return new ArrayList<>();
    }

    @Override
    public void save(ConfigurationSection section, String path, GUI.Builder<?, ?> value) {

    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<GUI.Builder<?, ?>> value) {

    }


    private GUI.Builder<?, ?> getBuilder(String type) {
        final GUI.Type guiType = GUI.Type.valueOf(type.toUpperCase());
        return switch (guiType) {
            case CHEST -> GUI.chestBuilder();
            case HOPPER -> GUI.hopperBuilder();
            case DROPPER -> GUI.dropperBuilder();
            case FURNACE -> GUI.furnaceBuilder();
            case POTION -> GUI.potionBuilder();
            case SMITHING_TABLE -> GUI.smithingBuilder();
            case WORK_BENCH -> GUI.workBenchBuilder();
            case ENCHANTING -> GUI.enchantingBuilder();
            case ANVIL -> GUI.anvilBuilder();
            case LOOM -> GUI.loomBuilder();
            case CARTOGRAPHY -> GUI.cartographyBuilder();
            case GRINDSTONE -> GUI.grindstoneBuilder();
            case STONECUTTER -> GUI.stonecutterBuilder();
            case MERCHANT -> GUI.merchantBuilder();
            case BEACON -> GUI.beaconBuilder();
            case PAGINATED -> GUI.paginatedBuilder(this.guiManager);
            default -> throw new IllegalStateException("Unexpected value: " + guiType);
        };
    }

}
