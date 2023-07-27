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
import io.github.fisher2911.guiconfig.metadata.BuiltInMetadata;
import io.github.fisher2911.message.MessageHandler;

public class GUIMessageAction implements GUIClickAction {

    public static final String ID = "message";

    private final MessageHandler messageHandler;
    private final String messagePath;

    public GUIMessageAction(
            MessageHandler messageHandler,
            String messagePath
    ) {
        this.messageHandler = messageHandler;
        this.messagePath = messagePath;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void onClick(GUIClickEvent event) {
        final Object[] placeholders = event.getGUI().getMetadata().get(BuiltInMetadata.PLACEHOLDERS_KEY);
        if (placeholders == null) {
            this.messageHandler.sendMessage(event.getPlayer(), this.messagePath);
            return;
        }
        this.messageHandler.sendMessage(event.getPlayer(), this.messagePath, placeholders);
    }

}
