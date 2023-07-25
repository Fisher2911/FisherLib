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

package io.github.fisher2911.command.argument;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlayerArgument extends Argument<Player> {

    public static final String ID = "player";

    public static PlayerArgument of(Function<ArgumentReader, ArgumentResult<Player>> function) {
        return new PlayerArgument(function);
    }

    public static PlayerArgument create() {
        return new PlayerArgument(reader -> {
            final Player value = Bukkit.getPlayerExact(reader.next());
            if (value == null) {
                reader.previous();
                return ArgumentResult.failure("Invalid player");
            }
            return ArgumentResult.success(value);
        });
    }

    private PlayerArgument(Function<ArgumentReader, ArgumentResult<Player>> function) {
        super(ID, function);
    }

    @Override
    public ArgumentResult<Player> parse(ArgumentReader reader) {
        return this.function.apply(reader);
    }

    @Override
    public Class<Player> getType() {
        return Player.class;
    }

    @Override
    public int getPriority(Argument<?> argument) {
        final Class<?> type = argument.getType();
        if (type == Player.class) return 0;
        if (type == Double.class) return -1;
        if (type == Float.class) return -1;
        if (type == Integer.class) return -1;
        return argument.getPriority(this);
    }

    @Override
    public @Nullable List<String> getTabCompletions(List<Object> previousArgs, String currentArg, CommandSender sender) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(currentArg.toLowerCase()))
                .collect(Collectors.toList());
    }

}
