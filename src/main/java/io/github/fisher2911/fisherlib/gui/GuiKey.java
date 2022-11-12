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

package io.github.fisher2911.fisherlib.gui;

import io.github.fisher2911.fisherlib.FishPlugin;
import io.github.fisher2911.fisherlib.FisherLib;
import io.github.fisher2911.fisherlib.user.CoreUser;
import io.github.fisher2911.fisherlib.util.Metadata;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class GuiKey {

    private final JavaPlugin plugin;
    private final String id;

    private GuiKey(JavaPlugin plugin, String id) {
        this.plugin = plugin;
        this.id = id;
    }

    public static GuiKey key(JavaPlugin plugin, String id) {
        return new GuiKey(plugin, id);
    }

    private static final FisherLib PLUGIN = FisherLib.getPlugin(FisherLib.class);

    public static final GuiKey GUI = key(PLUGIN, "gui");
    public static final GuiKey PERMISSION = key(PLUGIN, "permission");
    public static final GuiKey USER = key(PLUGIN, "user");
    public static final GuiKey UPGRADE_ID = key(PLUGIN, "upgrade_id");
    public static final GuiKey MAX_LEVEL_ITEM = key(PLUGIN, "max_level_item");
    public static final GuiKey PREVIOUS_MENU_ID = key(PLUGIN, "previous_menu_id");
    public static final GuiKey SEND_DATA_KEYS = key(PLUGIN, "send_data_keys");
    public static final GuiKey UPGRADEABLE = key(PLUGIN, "upgradeable");
    public static final GuiKey INCREASE_UPGRADE_LEVEL_CONSUMER = key(PLUGIN, "increase_upgrade_level_consumer");

    private static final Map<String, GuiKey> DEFAULTS = Map.of(
            GUI.id, GUI,
            PERMISSION.id, PERMISSION,
            USER.id, USER,
            UPGRADE_ID.id, UPGRADE_ID,
            MAX_LEVEL_ITEM.id, MAX_LEVEL_ITEM,
            PREVIOUS_MENU_ID.id, PREVIOUS_MENU_ID,
            SEND_DATA_KEYS.id, SEND_DATA_KEYS
    );

    public static Map<String, GuiKey> defaults() {
        return DEFAULTS;
    }

    public static GuiKey key(FishPlugin<?> plugin, String id, boolean checkDefaultFirst) {
        if (checkDefaultFirst) {
            final GuiKey key = DEFAULTS.get(id);
            if (key != null) {
                return key;
            }
        }
        return key(plugin, id);
    }

    private static final Map<GuiKey, BiConsumer<Metadata, List<Object>>> placeholderMap = new HashMap<>();

    public static void registerPlaceholderAccumulator(GuiKey key, BiConsumer<Metadata, List<Object>> accumulator) {
        placeholderMap.put(key, accumulator);
    }

    static {
        registerPlaceholderAccumulator(GUI, (metadata, list) -> {
            final BaseGui gui = metadata.get(GUI, BaseGui.class);
            if (gui == null) return;
            list.add(gui);
        });
        registerPlaceholderAccumulator(PERMISSION, (metadata, list) -> {
            final String permission = metadata.get(PERMISSION, String.class);
            if (permission == null) return;
            list.add(permission);
        });
        registerPlaceholderAccumulator(USER, (metadata, list) -> {
            final CoreUser user = metadata.get(USER, CoreUser.class);
            if (user == null) return;
            list.add(user);
        });
        registerPlaceholderAccumulator(UPGRADE_ID, (metadata, list) -> {
            final String upgradeId = metadata.get(UPGRADE_ID, String.class);
            if (upgradeId == null) return;
            list.add(upgradeId);
        });
        registerPlaceholderAccumulator(MAX_LEVEL_ITEM, (metadata, list) -> {
            final ConditionalItem conditionalItem = metadata.get(MAX_LEVEL_ITEM, ConditionalItem.class);
            if (conditionalItem == null) return;
            list.add(conditionalItem);
        });
        registerPlaceholderAccumulator(PREVIOUS_MENU_ID, (metadata, list) -> {
            final String previousMenuId = metadata.get(PREVIOUS_MENU_ID, String.class);
            if (previousMenuId == null) return;
            list.add(previousMenuId);
        });
    }

    public static List<Object> toPlaceholders(Metadata metadata) {
        final List<Object> placeholders = new ArrayList<>();
        placeholderMap.forEach((key, accumulator) -> accumulator.accept(metadata, placeholders));
        return placeholders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GuiKey guiKey = (GuiKey) o;
        return Objects.equals(plugin, guiKey.plugin) && Objects.equals(id, guiKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plugin, id);
    }

}
