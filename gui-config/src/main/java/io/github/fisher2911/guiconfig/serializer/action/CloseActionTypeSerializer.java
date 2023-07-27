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

package io.github.fisher2911.guiconfig.serializer.action;

import io.github.fisher2911.guiconfig.action.GUICloseAction;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CloseActionTypeSerializer implements ActionTypeSerializer<GUICloseAction> {

    public static final CloseActionTypeSerializer INSTANCE = new CloseActionTypeSerializer();

    private CloseActionTypeSerializer() {}

    @Override
    public @Nullable GUICloseAction load(ConfigurationSection section, String path) {
        return new GUICloseAction();
    }

    @Override
    public @NotNull List<GUICloseAction> loadList(ConfigurationSection section, String path) {
        return new ArrayList<>();
    }

    @Override
    public void save(ConfigurationSection section, String path, GUICloseAction value) {

    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<GUICloseAction> value) {

    }

}
