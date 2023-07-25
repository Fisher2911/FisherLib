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

import io.github.fisher2911.command.argument.Argument;
import io.github.fisher2911.command.argument.BooleanArgument;
import io.github.fisher2911.command.argument.DoubleArgument;
import io.github.fisher2911.command.argument.FloatArgument;
import io.github.fisher2911.command.argument.IntArgument;
import io.github.fisher2911.command.argument.ListArgument;
import io.github.fisher2911.command.argument.LiteralArgument;
import io.github.fisher2911.command.argument.LongArgument;
import io.github.fisher2911.command.argument.PlayerArgument;
import io.github.fisher2911.command.argument.StringArgument;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;

public class ArgumentSupplier {

    private final Map<String, Argument<?>> argumentIdMap;
    private final Map<Type, Argument<?>> argumentClassMap;

    public ArgumentSupplier(
            Map<String, Argument<?>> argumentIdMap,
            Map<Type, Argument<?>> argumentClassMap
    ) {
        this.argumentIdMap = argumentIdMap;
        this.argumentClassMap = argumentClassMap;
    }

    public @Nullable Argument<?> getArgument(String id) {
        return this.argumentIdMap.get(id);
    }

    public @Nullable Argument<?> getArgument(Type type) {
        return this.argumentClassMap.get(type);
    }

    public void addDefaults() {
        this.argument(StringArgument.create())
                .argument(LiteralArgument.create(), false)
                .argument(IntArgument.create())
                .argument(LongArgument.create())
                .argument(FloatArgument.create())
                .argument(DoubleArgument.create())
                .argument(BooleanArgument.create())
                .argument(PlayerArgument.create())
        ;
    }

    public ListArgument getListArgument(String delimiter, Type parameterClass) {
        final Argument<?> argument = this.getArgument(parameterClass);
        if (argument == null) {
            throw new IllegalArgumentException("No argument found for class " + parameterClass.getTypeName());
        }
        return ListArgument.create(delimiter, argument);
    }

    public ArgumentSupplier argument(Argument<?> argument, boolean addClass) {
        this.argumentIdMap.put(argument.getId(), argument);
        if (addClass) {
            this.argumentClassMap.put(argument.getType(), argument);
            final Class<?> primitive = argument.getPrimitiveType();
            if (primitive != null) {
                this.argumentClassMap.put(primitive, argument);
            }
        }
        return this;
    }

    public ArgumentSupplier argument(Argument<?> argument) {
        return this.argument(argument, true);
    }

}
