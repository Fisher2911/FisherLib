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
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class GUIEvent<T extends InventoryEvent, P extends JavaPlugin> extends Event implements Cancellable {

    private GUIManager<P> guiManager;
    private final GUI<P> gui;
    private final Player player;
    private boolean cancelled;
    private final T bukkitEvent;

    public GUIEvent(GUIManager<P> guiManager, GUI<P> gui, Player player, T bukkitEvent) {
        this(guiManager, gui, player, bukkitEvent, false);
    }

    public GUIEvent(GUIManager<P> guiManager, GUI<P> gui, Player player, T bukkitEvent, boolean async) {
        super(async);
        this.guiManager = guiManager;
        this.gui = gui;
        this.player = player;
        this.bukkitEvent = bukkitEvent;
    }

    public GUIManager<P> getGuiManager() {
        return this.guiManager;
    }

    public GUI<P> getGui() {
        return this.gui;
    }

    public Player getPlayer() {
        return this.player;
    }

    public T getBukkitEvent() {
        return this.bukkitEvent;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
        if (this.bukkitEvent instanceof final Cancellable cancellable) {
            cancellable.setCancelled(cancel);
        }
    }

}
