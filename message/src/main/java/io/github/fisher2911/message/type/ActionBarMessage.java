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

package io.github.fisher2911.message.type;

import io.github.fisher2911.message.Message;
import io.github.fisher2911.message.MessageHandler;
import org.bukkit.entity.Player;

public class ActionBarMessage implements Message<Player> {

    private final String message;

    public ActionBarMessage(String message) {
        this.message = message;
    }

    @Override
    public void send(Player sender, MessageHandler messageHandler, Object... parsePlaceholders) {
        messageHandler.sendActionBar(sender, this.message, parsePlaceholders);
    }

}