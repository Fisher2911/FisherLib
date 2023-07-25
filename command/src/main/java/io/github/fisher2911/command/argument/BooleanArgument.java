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

import io.github.fisher2911.common.util.BooleanUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BooleanArgument extends Argument<Boolean> {

    public static final String ID = "boolean";

    public static BooleanArgument of(Function<ArgumentReader, ArgumentResult<Boolean>> function) {
        return new BooleanArgument(function);
    }

    public static BooleanArgument create() {
        return new BooleanArgument(reader -> {
            final Boolean value = BooleanUtils.tryParseBoolean(reader.next());
            if (value == null) {
                reader.previous();
                return ArgumentResult.failure("Invalid boolean");
            }
            return ArgumentResult.success(value);
        });
    }

    private BooleanArgument(Function<ArgumentReader, ArgumentResult<Boolean>> function) {
        super(ID, function);
    }

    @Override
    public ArgumentResult<Boolean> parse(ArgumentReader reader) {
        return this.function.apply(reader);
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public int getPriority(Argument<?> argument) {
        final Class<?> type = argument.getType();
        if (type == Boolean.class) return 0;
        return -1;
    }

    @Override
    public @Nullable List<String> getTabCompletions(List<Object> previousArgs, String currentArg, CommandSender sender) {
        final List<String> completions = new ArrayList<>();
        if ("false".startsWith(currentArg)) completions.add("false");
        if ("true".startsWith(currentArg)) completions.add("true");
        return completions;
    }

}
