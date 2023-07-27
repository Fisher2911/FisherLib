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
import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUIItem;
import io.github.fisher2911.gui.gui.GUISlot;
import io.github.fisher2911.guiconfig.GUIItemManager;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class GUISetItemsAction implements GUIClickAction {

    public static final String ID = "set_items";

    private final GUIItemManager guiItemManager;
    private final Map<GUISlot, String> items;
    private final int duration;

    public GUISetItemsAction(GUIItemManager guiItemManager, Map<GUISlot, String> items, int duration) {
        this.guiItemManager = guiItemManager;
        this.items = items;
        this.duration = duration;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void onClick(GUIClickEvent event) {
        final Map<GUISlot, GUIItem> previousItems = new HashMap<>();
        final GUI gui = event.getGUI();
        for (final Map.Entry<GUISlot, String> entry : this.items.entrySet()) {
            final GUISlot guiSlot = entry.getKey();
            final GUIItem.Builder guiItemBuilder = this.guiItemManager.getGUIItem(entry.getValue());
            if (guiItemBuilder == null) continue;
            final GUIItem previousItem = gui.getItem(guiSlot);
            previousItems.put(guiSlot, previousItem);
            final GUIItem guiItem = guiItemBuilder.build();
            gui.setItem(guiSlot, guiItem);
            gui.update(guiItem);
        }
        if (this.duration <= 0) return;
        Bukkit.getScheduler().runTaskLater(gui.getPlugin(), () -> {
            for (final Map.Entry<GUISlot, GUIItem> entry : previousItems.entrySet()) {
                gui.setItem(entry.getKey(), entry.getValue());
                gui.update(entry.getValue());
            }
        }, this.duration);
    }

}
