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

public class TitleMessage implements Message<Player> {

    private final String title;
    private final String subtitle;
    private final int fadeInTicks;
    private final int stayTicks;
    private final int fadeOutTicks;

    public TitleMessage(String title, String subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeInTicks = fadeInTicks;
        this.stayTicks = stayTicks;
        this.fadeOutTicks = fadeOutTicks;
    }

    @Override
    public void send(Player sender, MessageHandler messageHandler, Object... parsePlaceholders) {
        messageHandler.sendTitle(
                sender,
                this.title,
                this.subtitle,
                this.fadeInTicks,
                this.stayTicks,
                this.fadeOutTicks,
                parsePlaceholders
        );
    }

}
