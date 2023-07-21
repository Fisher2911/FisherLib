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

import io.github.fisher2911.gui.gui.GUIManager;
import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUISlot;
import io.github.fisher2911.gui.gui.GUIItem;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GUIClickEvent<P extends JavaPlugin> extends GUIEvent<InventoryClickEvent, P> {

    private static final HandlerList handlers = new HandlerList();

    private final @Nullable GUISlot slot;
    private final @Nullable GUIItem<P> item;

    public GUIClickEvent(
            GUIManager<P> guiManager,
            GUI<P> gui,
            Player player,
            @Nullable GUISlot slot,
            InventoryClickEvent bukkitEvent
    ) {
        super(guiManager, gui, player, bukkitEvent);
        this.slot = slot;
        if (this.slot != null) {
            this.item = this.getGUI().getItem(this.slot);
        } else {
            this.item = null;
        }
    }

    public GUIClickEvent(GUIManager<P> guiManager, GUI<P> gui, Player player, InventoryClickEvent bukkitEvent, boolean async, @Nullable GUISlot slot) {
        super(guiManager, gui, player, bukkitEvent, async);
        this.slot = slot;
        if (this.slot != null) {
            this.item = this.getGUI().getItem(this.slot);
        } else {
            this.item = null;
        }
    }

    public @Nullable GUISlot getSlot() {
        return this.slot;
    }

    public @Nullable GUIItem<P> getItem() {
        return this.item;
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
