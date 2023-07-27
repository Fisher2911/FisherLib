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

package io.github.fisher2911.guiconfig.serializer;

import io.github.fisher2911.common.item.ItemBuilder;
import io.github.fisher2911.config.type.ItemBuilderTypeSerializer;
import io.github.fisher2911.config.type.TypeSerializer;
import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.guiconfig.action.GUIClickAction;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class GUIItemTypeSerializer implements TypeSerializer<GUIItem.Builder> {

    private static GUIItemTypeSerializer instance;

    public static GUIItemTypeSerializer getInstance(ClickActionTypeSerializer serializer) {
        if (instance == null) {
            instance = new GUIItemTypeSerializer(serializer);
        }
        return instance;
    }

    private final ClickActionTypeSerializer serializer;

    private GUIItemTypeSerializer(ClickActionTypeSerializer serializer) {
        this.serializer = serializer;
    }

    private static final String ITEM_PATH = "item";
    private static final String ACTIONS_PATH = "actions";
    private static final String CANCEL_CLICK_PATH = "cancel-click";

    @Override
    public @Nullable GUIItem.Builder load(ConfigurationSection section, String path) {
        final ItemBuilder itemBuilder = ItemBuilderTypeSerializer.INSTANCE.load(section, path + "." + ITEM_PATH);
        if (itemBuilder == null) throw new IllegalArgumentException("ItemBuilder cannot be null");
        final List<GUIClickAction> actions = this.serializer.loadList(section, path + "." + ACTIONS_PATH);
        final boolean cancelClick = section.getBoolean(path + "." + CANCEL_CLICK_PATH, false);
        final GUIItem.Builder builder = GUIItem.builder(itemBuilder).listenClick(e -> actions.forEach(a -> a.onClick(e)));
        if (cancelClick) {
            return builder.cancelClick().cancelDrag();
        }
        return builder;
    }

    @Override
    public @NotNull List<GUIItem.Builder> loadList(ConfigurationSection section, String path) {
        return new ArrayList<>();
    }

    @Override
    public void save(ConfigurationSection section, String path, GUIItem.Builder value) {

    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<GUIItem.Builder> value) {

    }

}
