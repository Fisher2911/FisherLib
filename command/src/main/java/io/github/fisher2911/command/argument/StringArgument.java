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

public class StringArgument extends Argument<String> {

    public static final String ID = "string";

    public static StringArgument of(Function<ArgumentReader, ArgumentResult<String>> function) {
        return new StringArgument(ID, function);
    }

    public static StringArgument create() {
        return new StringArgument(ID, reader -> ArgumentResult.success(reader.next()));
    }

    protected StringArgument(String id, Function<ArgumentReader, ArgumentResult<String>> function) {
        super(id, function);
    }

    @Override
    public ArgumentResult<String> parse(ArgumentReader reader) {
        return this.function.apply(reader);
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public int getPriority(Argument<?> argument) {
        return 1;
    }

    @Override
    public @Nullable List<String> getTabCompletions(List<Object> previousArgs, String currentArg, CommandSender sender) {
        return null;
    }

}
