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

package io.github.fisher2911.gui.gui.conditional;

import io.github.fisher2911.common.item.ItemBuilder;
import io.github.fisher2911.common.metadata.BuiltInMetadata;
import io.github.fisher2911.gui.gui.GUI;
import io.github.fisher2911.gui.gui.GUIItem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PermissionConditionalItemBuilder implements ConditionalItemBuilder {

    private final List<Entry> list;

    protected PermissionConditionalItemBuilder(List<Entry> list) {
        this.list = list;
        for (final var entry : list) {
            if (entry.permission() == null) return;
        }
        throw new IllegalStateException("No default item builder found");
    }

    @Override
    public @NotNull ItemBuilder getItemBuilder(GUI gui, GUIItem guiItem) {
        Player player = guiItem.getMetadata().get(BuiltInMetadata.PLAYER_KEY);
        if (player == null) {
            player = gui.getMetadata().get(BuiltInMetadata.PLAYER_KEY);
        }
        if (player == null) return this.list.get(list.size() - 1).itemBuilder();
        for (final var entry : list) {
            if (entry.permission() == null) continue;
            if (player.hasPermission(entry.permission())) return entry.itemBuilder();
        }
        return list.get(list.size() - 1).itemBuilder();
    }

    public static record Entry(@Nullable String permission, ItemBuilder itemBuilder) { }

}
