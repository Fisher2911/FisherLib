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

import io.github.fisher2911.common.metadata.Metadata;
import io.github.fisher2911.gui.event.GUIClickEvent;
import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.guiconfig.gui.GUISupplier;
import io.github.fisher2911.guiconfig.metadata.BuiltInMetadata;

import java.util.HashMap;

public class GUIOpenAction implements GUIClickAction {

    public static final String ID = "open";

    private final GUISupplier guiSupplier;
    private final String menu;

    public GUIOpenAction(GUISupplier guiSupplier, String menu) {
        this.guiSupplier = guiSupplier;
        this.menu = menu;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void onClick(GUIClickEvent event) {
        final GUI gui = this.guiSupplier.getGUIById(this.menu);
        if (gui == null) return;
        final Metadata metadata = new Metadata(new HashMap<>());
        final GUIItem item = event.getItem();
        if (item != null) {
            final Metadata transferMetadata = item.getMetadata().get(BuiltInMetadata.TRANSFER_METADATA_KEY);
            if (transferMetadata != null) {
                metadata.putAll(transferMetadata, false);
            }
        }
        final Metadata transferMetadata = event.getGUI().getMetadata().get(BuiltInMetadata.TRANSFER_METADATA_KEY);
        if (transferMetadata != null) {
            metadata.putAll(transferMetadata, false);
        }
        gui.getMetadata().putAll(metadata, false);
        event.getGuiManager().openGUI(gui, event.getPlayer());
    }

}
