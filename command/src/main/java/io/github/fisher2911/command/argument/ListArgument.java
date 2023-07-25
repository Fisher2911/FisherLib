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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListArgument extends Argument<List> {

    public static final String ID = "list";
    private final String delimiter;
    private final Argument<?> argument;

    private ListArgument(Function<ArgumentReader, ArgumentResult<List>> function, String delimiter, Argument<?> argument) {
        super(ID, function);
        this.delimiter = delimiter;
        this.argument = argument;
    }

    public static ListArgument create(String delimiter, Argument<?> argument) {
        return new ListArgument(reader -> {
            final List<Object> list = new ArrayList<>();
            while (reader.hasNext()) {
                try {
                    final ArgumentResult<?> result = argument.parse(reader);
                    if (result.isFailure()) break;
                    list.add(result.getResult());
                } catch (Exception e) {
                    reader.previous();
                }
                if (list.isEmpty()) {
                    return ArgumentResult.failure("Expected " + argument.getId() + " but got nothing");
                }
            }
            return ArgumentResult.success(list);
        }, delimiter, argument);
    }

    @Override
    public ArgumentResult<List> parse(ArgumentReader reader) {
        return this.function.apply(reader);
    }

    @Override
    public Class<List> getType() {
        return List.class;
    }

    @Override
    public int getPriority(Argument<?> argument) {
        if (argument.getType() == this.getType()) return 0;
        return -1;
    }

    @Override
    public @Nullable List<String> getTabCompletions(List<Object> previousArgs, String currentArg, CommandSender sender) {
//        final String[] split = currentArg.split(this.delimiter);
//        final String current = split[split.length - 1];
        return this.argument.getTabCompletions(previousArgs, currentArg, sender);
    }

}
