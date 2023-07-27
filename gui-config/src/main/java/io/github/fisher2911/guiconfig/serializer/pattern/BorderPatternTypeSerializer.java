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

package io.github.fisher2911.guiconfig.serializer.pattern;

import io.github.fisher2911.config.type.StringTypeSerializer;
import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.gui.gui.pattern.BorderPattern;
import io.github.fisher2911.gui.gui.pattern.Pattern;
import io.github.fisher2911.guiconfig.GUIItemManager;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BorderPatternTypeSerializer implements PatternTypeSerializer<BorderPattern> {

    private static BorderPatternTypeSerializer instance;

    public static BorderPatternTypeSerializer getInstance(GUIItemManager guiItemManager) {
        if (instance == null) {
            instance = new BorderPatternTypeSerializer(guiItemManager);
        }
        return instance;
    }

    private final GUIItemManager guiItemManager;

    private BorderPatternTypeSerializer(GUIItemManager guiItemManager) {
        this.guiItemManager = guiItemManager;
    }

    private static final String ITEMS_PATH = "items";
    private static final String PRIORITY_PATH = "priority";

    @Override
    public @Nullable BorderPattern load(ConfigurationSection section, String path) {
        final List<GUIItem> guiItems = StringTypeSerializer.INSTANCE.loadList(section, path + "." + ITEMS_PATH)
                .stream()
                .map(this.guiItemManager::getGUIItem)
                .filter(b -> b != null)
                .map(GUIItem.Builder::build)
                .collect(Collectors.toList());
        final int priority = section.getInt(path + "." + PRIORITY_PATH);
        return Pattern.borderPattern(guiItems, priority);
    }

    @Override
    public @NotNull List<BorderPattern> loadList(ConfigurationSection section, String path) {
        return new ArrayList<>();
    }

    @Override
    public void save(ConfigurationSection section, String path, BorderPattern value) {

    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<BorderPattern> value) {

    }

}
