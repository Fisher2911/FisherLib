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

import io.github.fisher2911.common.util.NumberUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class FloatArgument extends Argument<Float> {

    public static final String ID = "float";

    public static FloatArgument of(Function<ArgumentReader, ArgumentResult<Float>> function) {
        return new FloatArgument(function);
    }

    public static FloatArgument create() {
        return new FloatArgument(reader -> {
            final String next = reader.next();
            final Float value = NumberUtils.tryParseFloat(next);
            if (value == null) {
                reader.previous();
                return ArgumentResult.invalidArgument(next);
            };
            return ArgumentResult.success(value);
        });
    }

    private FloatArgument(Function<ArgumentReader, ArgumentResult<Float>> function) {
        super(ID, function);
    }

    @Override
    public ArgumentResult<Float> parse(ArgumentReader reader) {
        return this.function.apply(reader);
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

    @Override
    public int getPriority(Argument<?> argument) {
        final Class<?> type = argument.getType();
        if (type == Float.class) return 0;
        if (type == Double.class) return 1;
        if (type == IntArgument.class) return -1;
        if (type == LongArgument.class) return -1;
        return argument.getPriority(this);
    }

    @Override
    public @Nullable List<String> getTabCompletions(List<Object> previousArgs, String currentArg, CommandSender sender) {
        if (!currentArg.isEmpty() && NumberUtils.tryParseFloat(currentArg) == null) return null;
        return List.of(currentArg + (currentArg.contains(".") ? "0" : ".0"));
    }

}
