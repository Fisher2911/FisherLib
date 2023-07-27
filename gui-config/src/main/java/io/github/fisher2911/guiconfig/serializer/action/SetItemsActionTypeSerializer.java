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

package io.github.fisher2911.guiconfig.serializer.action;

import io.github.fisher2911.config.type.MapTypeSerializer;
import io.github.fisher2911.config.type.StringTypeSerializer;
import io.github.fisher2911.gui.gui.GUISlot;
import io.github.fisher2911.guiconfig.GUIItemManager;
import io.github.fisher2911.guiconfig.action.GUISetItemsAction;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SetItemsActionTypeSerializer implements ActionTypeSerializer<GUISetItemsAction> {

    private static SetItemsActionTypeSerializer instance;

    public static SetItemsActionTypeSerializer getInstance(GUIItemManager guiItemManager) {
        if (instance == null) {
            instance = new SetItemsActionTypeSerializer(guiItemManager);
        }
        return instance;
    }

    private final GUIItemManager guiItemManager;

    private SetItemsActionTypeSerializer(GUIItemManager guiItemManager) {
        this.guiItemManager = guiItemManager;
    }

    private static final String ITEMS_PATH = "items";
    private static final String DURATION_PATH = "duration";

    @Override
    public @Nullable GUISetItemsAction load(ConfigurationSection section, String path) {
        final int duration = section.getInt(DURATION_PATH, 0);
        final Map<GUISlot, String> map = MapTypeSerializer.create(StringTypeSerializer.INSTANCE)
                .load(section, ITEMS_PATH, s -> GUISlot.of(Integer.parseInt(s)));
        return new GUISetItemsAction(this.guiItemManager, map, duration);
    }

    @Override
    public @NotNull List<GUISetItemsAction> loadList(ConfigurationSection section, String path) {
        return new ArrayList<>();
    }

    @Override
    public void save(ConfigurationSection section, String path, GUISetItemsAction value) {

    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<GUISetItemsAction> value) {

    }

}
