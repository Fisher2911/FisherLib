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

package io.github.fisher2911.guiconfig.action;

import io.github.fisher2911.gui.event.GUIClickEvent;
import org.bukkit.entity.Player;

public class GUICommandAction implements GUIClickAction {

    public static final String ID = "command";

    private final String command;
    private final boolean playerCommand;

    public GUICommandAction(String command, boolean playerCommand) {
        this.command = command;
        this.playerCommand = playerCommand;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void onClick(GUIClickEvent event) {
        final Player player = event.getPlayer();
        if (this.playerCommand) {
            player.performCommand(command);
            return;
        }
        player.getServer().dispatchCommand(player.getServer().getConsoleSender(), command);
    }

}
