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

import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.gui.gui.pattern.PaginatedPattern;
import io.github.fisher2911.gui.gui.pattern.Pattern;
import io.github.fisher2911.guiconfig.GUIItemManager;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PaginatedPatternTypeSerializer implements PatternTypeSerializer<PaginatedPattern> {

    private static PaginatedPatternTypeSerializer instance;

    public static PaginatedPatternTypeSerializer getInstance(GUIItemManager guiItemManager) {
        if (instance == null) {
            instance = new PaginatedPatternTypeSerializer(guiItemManager);
        }
        return instance;
    }

    private final GUIItemManager guiItemManager;

    private PaginatedPatternTypeSerializer(GUIItemManager guiItemManager) {
        this.guiItemManager = guiItemManager;
    }

    private static final String PREVIOUS_PAGE_PATH = "previous-page-item";
    private static final String NEXT_PAGE_PATH = "next-page-item";
    private static final String PRIORITY_PATH = "priority";

    @Override
    public @Nullable PaginatedPattern load(ConfigurationSection section, String path) {
        final String previousPageItemId = section.getString(path + "." + PREVIOUS_PAGE_PATH);
        final String nextPageItemId = section.getString(path + "." + NEXT_PAGE_PATH);
        final int priority = section.getInt(path + "." + PRIORITY_PATH);
        final GUIItem.Builder previousPageItem = this.guiItemManager.getGUIItem(previousPageItemId);
        if (previousPageItem == null) {
            throw new IllegalArgumentException("Previous page item with id " + previousPageItemId + " not found");
        }
        final GUIItem.Builder nextPageItem = this.guiItemManager.getGUIItem(nextPageItemId);
        if (nextPageItem == null) {
            throw new IllegalArgumentException("Next page item with id " + nextPageItemId + " not found");
        }
        return Pattern.paginatedPattern(previousPageItem.build(), nextPageItem.build(), priority);
    }

    @Override
    public @NotNull List<PaginatedPattern> loadList(ConfigurationSection section, String path) {
        return new ArrayList<>();
    }

    @Override
    public void save(ConfigurationSection section, String path, PaginatedPattern value) {

    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<PaginatedPattern> value) {

    }

}
