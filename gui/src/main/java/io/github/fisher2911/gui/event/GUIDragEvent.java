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

package io.github.fisher2911.gui.event;

import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUIManager;
import io.github.fisher2911.gui.gui.GUISlot;
import io.github.fisher2911.gui.gui.GUIItem;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class GUIDragEvent<P extends JavaPlugin> extends GUIEvent<InventoryDragEvent, P> {

    private static final HandlerList handlers = new HandlerList();

    private final @Unmodifiable Map<GUISlot, GUIItem<P>> addedItems;
    private final @Unmodifiable Set<GUISlot> containerSlots;

    public GUIDragEvent(
            GUIManager<P> guiManager,
            GUI<P> gui,
            Player player,
            Map<GUISlot, GUIItem<P>> addedItems,
            Set<GUISlot> containerSlots,
            InventoryDragEvent bukkitEvent
    ) {
        this(guiManager, gui, player, addedItems, containerSlots, bukkitEvent, false);
    }

    public GUIDragEvent(
            GUIManager<P> guiManager,
            GUI<P> gui,
            Player player,
            Map<GUISlot, GUIItem<P>> addedItems,
            Set<GUISlot> containerSlots,
            InventoryDragEvent bukkitEvent,
            boolean async
    ) {
        super(guiManager, gui, player, bukkitEvent, async);
        this.addedItems = Collections.unmodifiableMap(addedItems);
        this.containerSlots = Collections.unmodifiableSet(containerSlots);
    }


    public @Unmodifiable Map<GUISlot, GUIItem<P>> getAddedItems() {
        return this.addedItems;
    }

    public @Unmodifiable Set<GUISlot> getContainerSlots() {
        return this.containerSlots;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
