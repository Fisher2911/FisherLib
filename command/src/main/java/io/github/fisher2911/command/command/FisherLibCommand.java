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

package io.github.fisher2911.command.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class FisherLibCommand extends BukkitCommand {

    private final BaseCommand baseCommand;

    public FisherLibCommand(BaseCommand baseCommand) {
        super(Objects.requireNonNull(baseCommand.getName(), "BaseCommand name cannot be null"));
        this.baseCommand = baseCommand;
    }

    @Override
    public boolean execute(@NotNull CommandSender commandSender, @NotNull String s, @NotNull String[] strings) {
        final String[] args = new String[strings.length + 1];
        args[0] = s;
        System.arraycopy(strings, 0, args, 1, strings.length);
        this.baseCommand.execute(commandSender, args);
        return true;
    }

    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] strings) throws IllegalArgumentException {
        final String[] args = new String[strings.length + 1];
        args[0] = this.baseCommand.getName();
        System.arraycopy(strings, 0, args, 1, strings.length);
        return this.baseCommand.getTabCompletions(sender, args);
    }

    public BaseCommand getBaseCommand() {
        return this.baseCommand;
    }

}
