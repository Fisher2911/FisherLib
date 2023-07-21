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
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

public class GUICloseEvent extends GUIEvent<InventoryCloseEvent> {

    private static final HandlerList handlers = new HandlerList();

    public GUICloseEvent(GUIManager guiManager, GUI gui, Player player, InventoryCloseEvent bukkitEvent) {
        super(guiManager, gui, player, bukkitEvent);
    }

    public GUICloseEvent(GUIManager guiManager, GUI gui, Player player, InventoryCloseEvent bukkitEvent, boolean async) {
        super(guiManager, gui, player, bukkitEvent, async);
    }

    @Override
    public void setCancelled(boolean cancel) {
        super.setCancelled(cancel);
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
