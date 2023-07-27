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

import io.github.fisher2911.config.type.TypeSerializer;
import io.github.fisher2911.guiconfig.GUIItemManager;
import io.github.fisher2911.guiconfig.action.GUIClickAction;
import io.github.fisher2911.guiconfig.gui.GUISupplier;
import io.github.fisher2911.guiconfig.serializer.action.ActionTypeSerializer;
import io.github.fisher2911.guiconfig.serializer.action.CloseActionTypeSerializer;
import io.github.fisher2911.guiconfig.serializer.action.CommandActionTypeSerializer;
import io.github.fisher2911.guiconfig.serializer.action.MessageActionTypeSerializer;
import io.github.fisher2911.guiconfig.serializer.action.OpenActionTypeSerializer;
import io.github.fisher2911.guiconfig.serializer.action.SetItemsActionTypeSerializer;
import io.github.fisher2911.message.MessageHandler;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClickActionTypeSerializer implements TypeSerializer<GUIClickAction> {

    private static ClickActionTypeSerializer instance;

    public static ClickActionTypeSerializer getInstance(
            Map<String, ActionTypeSerializer<?>> actionTypeSerializerMap,
            MessageHandler messageHandler,
            GUISupplier guiSupplier,
            GUIItemManager guiItemManager
    ) {
        if (instance == null) {
            instance = new ClickActionTypeSerializer(actionTypeSerializerMap, messageHandler, guiSupplier, guiItemManager);
        }
        return instance;
    }

    private final Map<String, ActionTypeSerializer<?>> actionTypeSerializerMap;

    public ClickActionTypeSerializer(
            Map<String, ActionTypeSerializer<?>> actionTypeSerializerMap,
            MessageHandler messageHandler,
            GUISupplier guiSupplier,
            GUIItemManager guiItemManager
    ) {
        this.actionTypeSerializerMap = actionTypeSerializerMap;
        this.actionTypeSerializerMap.put("close", CloseActionTypeSerializer.INSTANCE);
        this.actionTypeSerializerMap.put("command", CommandActionTypeSerializer.INSTANCE);
        this.actionTypeSerializerMap.put("message", MessageActionTypeSerializer.getInstance(messageHandler));
        this.actionTypeSerializerMap.put("open", OpenActionTypeSerializer.getInstance(guiSupplier));
        this.actionTypeSerializerMap.put("set-items", SetItemsActionTypeSerializer.getInstance(guiItemManager));
    }

    private static final String TYPE_PATH = "type";

    @Override
    public @Nullable GUIClickAction load(ConfigurationSection section, String path) {
        String type = section.getString(path + "." + TYPE_PATH);
        if (type == null) return null;
        ActionTypeSerializer<?> actionTypeSerializer = this.actionTypeSerializerMap.get(type);
        if (actionTypeSerializer == null) return null;
        return actionTypeSerializer.load(section, path);
    }

    @Override
    public @NotNull List<GUIClickAction> loadList(ConfigurationSection section, String path) {
        final List<GUIClickAction> list = new ArrayList<>();
        for (final String key : section.getKeys(false)) {
            final GUIClickAction action = this.load(section, key);
            if (action == null) continue;
            list.add(action);
        }
        return list;
    }

    @Override
    public void save(ConfigurationSection section, String path, GUIClickAction value) {

    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<GUIClickAction> value) {

    }

}
