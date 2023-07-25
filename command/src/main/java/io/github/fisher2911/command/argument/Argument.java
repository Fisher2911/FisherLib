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
import java.util.Map;
import java.util.function.Function;

public abstract class Argument<T> {

    private static final Map<Class<?>, Class<?>> CLASS_TO_PRIMITIVE_MAP = Map.of(
            Byte.class, byte.class,
            Short.class, short.class,
            Integer.class, int.class,
            Long.class, long.class,
            Float.class, float.class,
            Double.class, double.class,
            Boolean.class, boolean.class
    );

    protected final Function<ArgumentReader, ArgumentResult<T>> function;
    protected final String id;

    public Argument(String id, Function<ArgumentReader, ArgumentResult<T>> function) {
        this.id = id;
        this.function = function;
    }

    public abstract ArgumentResult<T> parse(ArgumentReader reader);

    public abstract Class<T> getType();

    public @Nullable Class<?> getPrimitiveType() {
        return CLASS_TO_PRIMITIVE_MAP.get(this.getType());
    }

    public abstract int getPriority(Argument<?> argument);

    public String getId() {
        return this.id;
    }

    public abstract @Nullable List<String> getTabCompletions(List<Object> previousArgs, String currentArg, CommandSender sender);

}
