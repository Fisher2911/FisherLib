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

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class LiteralArgument extends StringArgument {

    public static final String ID = "literal";

    private final String match;

    private LiteralArgument(Function<ArgumentReader, ArgumentResult<String>> function, String match) {
        super(ID, function);
        this.match = match;
    }

    public static LiteralArgument literal(String match) {
        return new LiteralArgument(reader -> {
            final String next = reader.next();
            if (next.equalsIgnoreCase(match)) {
                return ArgumentResult.success(next);
            }
            reader.previous();
            return ArgumentResult.invalidArgument(next);
        }, match);
    }

    public String getMatch() {
        return this.match;
    }

    @Override
    public int getPriority(Argument<?> argument) {
        if (argument instanceof LiteralArgument) {
            return 0;
        }
        return -1;
    }

    @Override
    public @Nullable List<String> getTabCompletions(List<Object> previousArgs, String currentArg, CommandSender sender) {
        if (this.match.startsWith(currentArg)) {
            return List.of(this.match);
        }
        return null;
    }

}
