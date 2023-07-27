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

import io.github.fisher2911.guiconfig.action.GUICommandAction;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandActionTypeSerializer implements ActionTypeSerializer<GUICommandAction> {

    public static final CommandActionTypeSerializer INSTANCE = new CommandActionTypeSerializer();

    private CommandActionTypeSerializer() {}

    private static final String COMMAND_PATH = "command";
    private static final String AS_PLAYER_PATH = "as-player";

    @Override
    public @Nullable GUICommandAction load(ConfigurationSection section, String path) {
        final String command = section.getString(path + "." + COMMAND_PATH);
        if (command == null) throw new IllegalArgumentException("CommandActionSerializer: command cannot be null");
        final boolean asPlayer = section.getBoolean(path + "." + AS_PLAYER_PATH, false);
        return new GUICommandAction(
                command,
                asPlayer
        );
    }

    @Override
    public @NotNull List<GUICommandAction> loadList(ConfigurationSection section, String path) {
        return new ArrayList<>();
    }

    @Override
    public void save(ConfigurationSection section, String path, GUICommandAction value) {

    }

    @Override
    public void saveList(ConfigurationSection section, String path, List<GUICommandAction> value) {

    }

}
