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

package io.github.fisher2911.gui.gui;

import io.github.fisher2911.gui.event.GUIClickEvent;
import io.github.fisher2911.gui.event.GUICloseEvent;
import io.github.fisher2911.gui.event.GUIDragEvent;
import io.github.fisher2911.gui.event.GUIOpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class GUIManager implements Listener {

    private final JavaPlugin plugin;
    private final Map<UUID, GUI> guiViewers;

    public GUIManager(Map<UUID, GUI> guiViewers) {
        this.plugin = JavaPlugin.getProvidingPlugin(this.getClass());
        this.guiViewers = guiViewers;
    }


    public void openGUI(GUI gui, Player player) {
        this.openGUI(gui, List.of(player));
    }

    public void openGUI(GUI gui, Collection<Player> players) {
        if (gui instanceof final PaginatedGUI paginatedGUI) {
            this.openPaginatedGUI(paginatedGUI, players);
            return;
        }
        if (gui.getOwner(this.plugin) == null) {
            players.forEach(player -> this.guiViewers.put(player.getUniqueId(), gui));
        }
        gui.open(players);
    }

    public void openPaginatedGUI(PaginatedGUI paginatedGUI, Player player) {
        this.openPaginatedGUI(paginatedGUI, List.of(player));
    }

    public void openPaginatedGUI(PaginatedGUI paginatedGUI, Collection<Player> players) {
        players.forEach(player -> this.guiViewers.put(player.getUniqueId(), paginatedGUI));
        this.openGUI(paginatedGUI.getCurrentGUI(), players);
    }

    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof final Player player)) return;
        final GUI gui = this.getCurrentGUI(player);
        if (gui == null) return;
        final GUIOpenEvent guiOpenEvent = new GUIOpenEvent(this, gui, player, event);
        gui.handle(guiOpenEvent, GUIOpenEvent.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof final Player player)) return;
        final GUI gui = this.getCurrentGUI(player);
        if (gui == null) return;
        final GUICloseEvent guiCloseEvent = new GUICloseEvent(this, gui, player, event);
        gui.handle(guiCloseEvent, GUICloseEvent.class);
        if (guiCloseEvent.isCancelled()) {
            Bukkit.getScheduler().runTaskLater(this.getPlugin(), () -> gui.open(player), 1);
            return;
        }
        if (this.guiViewers.get(player.getUniqueId()) instanceof final PaginatedGUI paginatedGUI) {
            if (paginatedGUI.isSwitchingPages()) return;
            paginatedGUI.removeViewer(player);
            for (final GUI page : paginatedGUI.getPages()) {
                this.tryCloseGUI(page);
            }
        }
        this.guiViewers.remove(player.getUniqueId());
        gui.removeViewer(player);
        this.tryCloseGUI(gui);
    }

    private void tryCloseGUI(GUI gui) {
        if (!gui.getViewers().isEmpty()) return;
        final GUITimer<? extends GUI> timer = gui.getTimer();
        if (timer == null) return;
        timer.stop();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof final Player player)) return;
        final GUI gui = this.getCurrentGUI(player);
        if (gui == null) return;
        final GUISlot slot;
        if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
            slot = this.getSlotFromClick(event.getClickedInventory(), event.getSlot());
        } else {
            slot = null;
        }
        final GUIClickEvent guiClickEvent = new GUIClickEvent(this, gui, player, slot, event);
        gui.handle(guiClickEvent, GUIClickEvent.class);
        final GUIItem guiItem = gui.getItem(slot);
        if (guiItem == null) return;
        guiItem.handle(guiClickEvent, GUIClickEvent.class);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof final Player player)) return;
        final GUI gui = this.getCurrentGUI(player);
        if (gui == null) return;
        final Inventory inventory = event.getInventory();
        final GUIDragEvent guiDragEvent = new GUIDragEvent(
                this,
                gui,
                player,
                event.getNewItems().entrySet()
                        .stream()
                        .map(entry -> {
                            final GUISlot slot;
                            if (inventory.equals(event.getView().getTopInventory())) {
                                slot = this.getSlotFromClick(inventory, entry.getKey());
                            } else {
                                slot = null;
                            }
                            if (slot == null) return null;
                            return Map.entry(slot, GUIItem.builder(entry.getValue()).build());
                        })
                        .filter(entry -> entry != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)),
                event.getInventorySlots()
                        .stream()
                        .map(slot -> this.getSlotFromClick(inventory, slot))
                        .filter(slot -> slot != null)
                        .collect(Collectors.toSet()),
                event
        );
        gui.handle(guiDragEvent, GUIDragEvent.class);
    }

    @Nullable
    private GUISlot getSlotFromClick(@Nullable Inventory inventory, int slot) {
        if (inventory == null) return null;
        final InventoryType inventoryType = inventory.getType();
        return switch (inventoryType) {
            case BREWING -> switch (slot) {
                case 0 -> GUISlot.Potion.ZERO;
                case 1 -> GUISlot.Potion.ONE;
                case 2 -> GUISlot.Potion.TWO;
                case 3 -> GUISlot.Potion.INGREDIENT;
                case 4 -> GUISlot.Potion.FUEL;
                default -> GUISlot.of(slot);
            };
            case WORKBENCH -> switch (slot) {
                case 0 -> GUISlot.WorkBench.RESULT;
                default -> GUISlot.of(slot);
            };
            case FURNACE -> switch (slot) {
                case 0 -> GUISlot.Furnace.SMELTING;
                case 1 -> GUISlot.Furnace.FUEL;
                case 2 -> GUISlot.Furnace.RESULT;
                default -> GUISlot.of(slot);
            };
            case SMITHING -> GUISlot.SmithingTable.fromNum(slot);
            case ENCHANTING -> switch (slot) {
                case 0 -> GUISlot.EnchantingTable.ITEM;
                case 1 -> GUISlot.EnchantingTable.LAPIS;
                default -> GUISlot.of(slot);
            };
            case ANVIL -> switch (slot) {
                case 0 -> GUISlot.Anvil.FIRST;
                case 1 -> GUISlot.Anvil.SECOND;
                case 2 -> GUISlot.Anvil.RESULT;
                default -> GUISlot.of(slot);
            };
            case LOOM -> switch (slot) {
                case 0 -> GUISlot.Loom.BANNER;
                case 1 -> GUISlot.Loom.DYE;
                case 2 -> GUISlot.Loom.PATTERN;
                case 3 -> GUISlot.Loom.RESULT;
                default -> GUISlot.of(slot);
            };
            case CARTOGRAPHY -> switch (slot) {
                case 0 -> GUISlot.CartographyTable.MAP;
                case 1 -> GUISlot.CartographyTable.PAPER;
                case 2 -> GUISlot.CartographyTable.OUTPUT;
                default -> GUISlot.of(slot);
            };
            case GRINDSTONE -> switch (slot) {
                case 0 -> GUISlot.Grindstone.FIRST;
                case 1 -> GUISlot.Grindstone.SECOND;
                case 2 -> GUISlot.Grindstone.RESULT;
                default -> GUISlot.of(slot);
            };
            case STONECUTTER -> switch (slot) {
                case 0 -> GUISlot.Stonecutter.INPUT;
                case 1 -> GUISlot.Stonecutter.RESULT;
                default -> GUISlot.of(slot);
            };
            case MERCHANT -> switch (slot) {
                case 0 -> GUISlot.Merchant.FIRST;
                case 1 -> GUISlot.Merchant.SECOND;
                case 2 -> GUISlot.Merchant.RESULT;
                default -> GUISlot.of(slot);
            };
            default -> GUISlot.of(slot);
        };
    }

    private @Nullable GUI getCurrentGUI(Player player) {
        final GUI gui = this.guiViewers.get(player.getUniqueId());
        if (gui instanceof final PaginatedGUI paginatedGUI) {
            return paginatedGUI.getCurrentGUI();
        }
        return gui;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final GUI gui = this.guiViewers.remove(player.getUniqueId());
        if (gui == null) return;
        gui.removeViewer(player);
    }

}
