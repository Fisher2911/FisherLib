/*
 *     Kingdoms Plugin
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

package io.github.fisher2911.fisherlib.config.serializer;

import io.github.fisher2911.fisherlib.FishPlugin;
import io.github.fisher2911.fisherlib.command.CommandSenderType;
import io.github.fisher2911.fisherlib.gui.BaseGui;
import io.github.fisher2911.fisherlib.gui.BaseGuiItem;
import io.github.fisher2911.fisherlib.gui.ClickAction;
import io.github.fisher2911.fisherlib.gui.ConditionalItem;
import io.github.fisher2911.fisherlib.gui.GuiKey;
import io.github.fisher2911.fisherlib.gui.GuiManager;
import io.github.fisher2911.fisherlib.gui.wrapper.InventoryEventWrapper;
import io.github.fisher2911.fisherlib.user.CoreUser;
import io.github.fisher2911.fisherlib.user.CoreUserManager;
import io.github.fisher2911.fisherlib.util.EnumUtil;
import io.github.fisher2911.fisherlib.util.function.TriFunction;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ClickActionSerializer {

    private static final Map<String, TriFunction<FishPlugin<?>, ConfigurationNode, Set<ClickType>, Consumer<InventoryEventWrapper<InventoryClickEvent>>>> clickActionLoaders =
            new HashMap<>();

    public static void registerLoader(String clickAction, TriFunction<FishPlugin<?>, ConfigurationNode, Set<ClickType>, Consumer<InventoryEventWrapper<InventoryClickEvent>>> loader) {
        clickActionLoaders.put(clickAction, loader);
    }

    static {
        registerLoader(ClickAction.NEXT_PAGE, ClickActionSerializer::loadNextPage);
        registerLoader(ClickAction.PREVIOUS_PAGE, ClickActionSerializer::loadPreviousPage);
        registerLoader(ClickAction.PLAYER_COMMAND, ClickActionSerializer::loadPlayerCommand);
        registerLoader(ClickAction.CONSOLE_COMMAND, ClickActionSerializer::loadConsoleCommand);
        registerLoader(ClickAction.OPEN_MENU, ClickActionSerializer::loadOpenMenu);
        registerLoader(ClickAction.INCREASE_UPGRADE_LEVEL, ClickActionSerializer::loadIncreaseLevel);
        registerLoader(ClickAction.SET_ITEMS, ClickActionSerializer::loadSetItems);
        registerLoader(ClickAction.SET_ITEM, ClickActionSerializer::loadSetItem);
        registerLoader(ClickAction.CLOSE_MENU, ClickActionSerializer::loadCloseMenu);
        registerLoader(ClickAction.SEND_DATA, ClickActionSerializer::loadSendData);
        registerLoader(ClickAction.PREVIOUS_GUI, ClickActionSerializer::loadPreviousGui);
    }

    private static final String CLICK_TYPES_PATH = "click-types";
    private static final String COMMAND_PATH = "command";
    private static final String MENU_PATH = "menu";
    private static final String RESET_DELAY_PATH = "duration";

    public static List<Consumer<InventoryEventWrapper<InventoryClickEvent>>> deserializeAll(FishPlugin<?> plugin, ConfigurationNode source) throws SerializationException {
        final List<Consumer<InventoryEventWrapper<InventoryClickEvent>>> actions = new ArrayList<>();
        for (var actionsEntry : source.childrenMap().entrySet()) {
            final var node = actionsEntry.getValue();
            for (var entry : node.childrenMap().entrySet()) {
                if (!(entry.getKey() instanceof final String action)) continue;
                final var consumer = deserialize(plugin, node.node(action), action.toUpperCase(Locale.ROOT));
                if (consumer == null) continue;
                actions.add(consumer);
            }
        }
        return actions;
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> deserialize(
            FishPlugin<?> plugin,
            ConfigurationNode source,
            String actionType
    ) throws SerializationException {
        final Set<ClickType> clickTypes = source.node(CLICK_TYPES_PATH).getList(String.class, new ArrayList<>())
                .stream()
                .map(s -> EnumUtil.valueOf(ClickType.class, s))
                .filter(clickType -> clickType != null)
                .collect(Collectors.toSet());
        final var loader = clickActionLoaders.get(actionType);
        if (loader == null) {
            throw new SerializationException("No loader for click action: " + actionType);
        }
        return loader.apply(plugin, source, clickTypes);
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadNextPage(FishPlugin<?> plugin, ConfigurationNode source, Set<ClickType> clickTypes) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            wrapper.gui().goToNextPage();
        };
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadPreviousPage(FishPlugin<?> plugin, ConfigurationNode source, Set<ClickType> clickTypes) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            wrapper.gui().goToPreviousPage();
        };
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadCommand(FishPlugin<?> plugin, ConfigurationNode source, Set<ClickType> clickTypes, CommandSenderType type) {
        if (type == CommandSenderType.ANY) throw new IllegalArgumentException("Command sender type cannot be ANY");
        final String command = source.node(COMMAND_PATH).getString();
        if (command == null) throw new IllegalArgumentException("Command cannot be null");
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final String parsed = plugin.getPlaceholders().apply(command, wrapper.gui());
            if (type == CommandSenderType.CONSOLE) {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), parsed);
            } else {
                Bukkit.getServer().dispatchCommand(event.getWhoClicked(), parsed);
            }
        };
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadPlayerCommand(FishPlugin<?> plugin, ConfigurationNode source, Set<ClickType> clickTypes) {
        return loadCommand(plugin, source, clickTypes, CommandSenderType.PLAYER);
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadConsoleCommand(FishPlugin<?> plugin, ConfigurationNode source, Set<ClickType> clickTypes) {
        return loadCommand(plugin, source, clickTypes, CommandSenderType.CONSOLE);
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadOpenMenu(FishPlugin<?> plugin, ConfigurationNode source, Set<ClickType> clickTypes) {
        final String menu = source.node(MENU_PATH).getString();
        if (menu == null) throw new IllegalArgumentException("Menu cannot be null");
        final GuiManager guiManager = plugin.getGuiManager();
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final BaseGui gui = wrapper.gui();
            final Map<Object, Object> metadata = new HashMap<>();
            List<String> previousIds = gui.getMetadata(GuiKey.PREVIOUS_MENU_ID, List.class);
            if (previousIds == null) previousIds = new ArrayList<>();
            if (previousIds.contains(gui.getId())) {
                previousIds.remove(gui.getId());
            } else {
                previousIds.add(gui.getId());
            }
            metadata.put(GuiKey.PREVIOUS_MENU_ID, previousIds);
            final BaseGuiItem item = gui.getBaseGuiItem(event.getSlot());
            if (item != null) {
                final List<String> keys = item.getMetadata(GuiKey.SEND_DATA_KEYS, List.class);
                final ConditionalItem conditionalItem = gui.getItem(event.getSlot());
                if (keys != null) {
                    for (String key : keys) {
//                        final GuiKey guiKey = EnumUtil.valueOf(GuiKey.class, key);
                        GuiKey guiKey = GuiKey.defaults().get(key);
                        if (guiKey == null) guiKey = GuiKey.key(plugin, key);
                        Object toSend = item.getMetadata(guiKey);
                        if (toSend == null) {
                            if (conditionalItem == null) continue;
                            toSend = conditionalItem.getMetadata(guiKey);
                            if (toSend == null) continue;
                        }
                        metadata.put(guiKey, toSend);
                    }
                }
            }
            guiManager.open(menu, wrapper.gui().getMetadata(GuiKey.USER, CoreUser.class), metadata, metadata.keySet());
        };
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadIncreaseLevel(FishPlugin<?> plugin, ConfigurationNode source, Set<ClickType> clickTypes) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final int clicked = event.getSlot();
            final BaseGuiItem item = wrapper.gui().getBaseGuiItem(clicked);
            if (item == null) return;
            final Consumer<InventoryEventWrapper<InventoryClickEvent>> increaser = item.getMetadata(GuiKey.INCREASE_UPGRADE_LEVEL_CONSUMER, Consumer.class);
            if (increaser == null) return;
            increaser.accept(wrapper);
        };
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadCloseMenu(FishPlugin<?> plugin, ConfigurationNode source, Set<ClickType> clickTypes) {
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            event.getWhoClicked().closeInventory();
        };
    }

    private static final String ITEMS_PATH = "items";

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadSetItems(
            FishPlugin<?> plugin,
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        // stupid checked exceptions not working with lambdas
        try {
            final Map<Integer, ConditionalItem> items = new HashMap<>();
            final int duration = source.node(RESET_DELAY_PATH).getInt(-1);
            for (var entry : source.node(ITEMS_PATH).childrenMap().entrySet()) {
                if (!(entry.getKey() instanceof final Integer slot)) continue;
                final ConditionalItem item = GuiItemSerializer.INSTANCE.deserialize(plugin, entry.getValue());
                if (item == null) continue;
                items.put(slot, item);
            }
            return wrapper -> {
                final var event = wrapper.event();
                event.setCancelled(true);
                if (!clickTypes.contains(event.getClick())) return;
                final Map<Integer, ConditionalItem> original = new HashMap<>();
                for (var entry : items.entrySet()) {
                    final int slot = entry.getKey();
                    final ConditionalItem conditionalItem = wrapper.gui().getItem(slot);
                    if (duration != -1) original.put(slot, conditionalItem);
                    wrapper.gui().set(entry.getKey(), entry.getValue());
                }
                if (duration == -1) return;
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    for (var entry : original.entrySet()) {
                        wrapper.gui().set(entry.getKey(), entry.getValue());
                    }
                }, duration);
            };
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String ITEM_PATH = "item";

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadSetItem(
            FishPlugin<?> plugin,
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        // stupid checked exceptions not working with lambdas
        try {
            final int duration = source.node(RESET_DELAY_PATH).getInt(-1);
            final ConditionalItem item = GuiItemSerializer.INSTANCE.deserialize(plugin, source/*.node(ITEM_PATH)*/);
            if (item == null) throw new SerializationException("Item cannot be null");
            return wrapper -> {
                final var event = wrapper.event();
                event.setCancelled(true);
                if (!clickTypes.contains(event.getClick())) return;
                final ConditionalItem original = wrapper.gui().getItem(event.getSlot());
                final int clicked = event.getSlot();
                final BaseGuiItem originalItem = wrapper.gui().getBaseGuiItem(clicked);
                final ConditionalItem.Builder builder = originalItem == null ? null : ConditionalItem.builder(item);
                if (originalItem != null) {
                    builder.metadata(originalItem.getMetadata().get(), true);
                }
                wrapper.gui().set(clicked, builder.build());
                if (duration == -1) return;
                Bukkit.getScheduler().runTaskLater(
                        plugin,
                        () -> wrapper.gui().set(clicked, original),
                        duration
                );
            };
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String DATA_PATH = "data";

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadSendData(
            FishPlugin<?> plugin,
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        // stupid checked exceptions not working with lambdas
        try {
            final List<String> keys = source.node(DATA_PATH).getList(String.class, new ArrayList<>())
                    .stream()
                    .map(String::toUpperCase).
                    collect(Collectors.toList());
            return wrapper -> {
                final var event = wrapper.event();
                event.setCancelled(true);
                if (!clickTypes.contains(event.getClick())) return;
                final int clicked = event.getSlot();
                final BaseGuiItem item = wrapper.gui().getBaseGuiItem(clicked);
                if (item == null) return;
                item.setMetadata(GuiKey.SEND_DATA_KEYS, keys);
            };
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

    private static Consumer<InventoryEventWrapper<InventoryClickEvent>> loadPreviousGui(
            FishPlugin<?> plugin,
            ConfigurationNode source,
            Set<ClickType> clickTypes
    ) {
        final CoreUserManager<?> userManager = plugin.getUserManager();
        return wrapper -> {
            final var event = wrapper.event();
            event.setCancelled(true);
            if (!clickTypes.contains(event.getClick())) return;
            final List<String> previousGuis = wrapper.gui().getMetadata(GuiKey.PREVIOUS_MENU_ID, List.class);
            if (previousGuis == null || previousGuis.isEmpty()) return;
            final String previousGui = previousGuis.remove(previousGuis.size() - 1);
            if (previousGui == null) return;
            final CoreUser user = userManager.forceGet(event.getWhoClicked().getUniqueId());
            if (user == null) return;
            plugin.getGuiManager().open(previousGui, user, Map.of(GuiKey.PREVIOUS_MENU_ID, previousGuis), Set.of());
        };
    }
    
}
