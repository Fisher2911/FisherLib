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
import io.github.fisher2911.command.argument.ArgumentResult;
import io.github.fisher2911.command.argument.LiteralArgument;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandBuilder {

    private final @Nullable CommandBuilder parent;
    private final @NotNull Argument<?> argument;
    private final List<CommandBuilder> children;
    private @Nullable String permission;
    private @Nullable Consumer<List<Object>> command;
    private @Nullable TriConsumer<? extends CommandSender, ArgumentResult<?>, String[]> errorHandler;

    public static CommandBuilder create(String name) {
        return new CommandBuilder(null, LiteralArgument.literal(name));
    }

    public static CommandBuilder create(String name, @Nullable String permission) {
        return new CommandBuilder(null, LiteralArgument.literal(name), permission);
    }

    private CommandBuilder(@Nullable CommandBuilder parent, @NotNull Argument<?> argument) {
        this.parent = parent;
        this.argument = argument;
        this.children = new ArrayList<>();
    }

    private CommandBuilder(@Nullable CommandBuilder parent, @NotNull Argument<?> argument, @Nullable String permission) {
        this.parent = parent;
        this.argument = argument;
        this.children = new ArrayList<>();
        this.permission = permission;
    }

    public CommandBuilder child(Consumer<CommandBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    public <T> CommandBuilder argument(Argument<T> argument) {
        return this.addChild(new CommandBuilder(this, argument));
    }

    public CommandBuilder permission(String permission) {
        this.permission = permission;
        return this;
    }

    public CommandBuilder addChild(CommandBuilder child) {
        this.children.add(child);
        return child;
    }

    public CommandBuilder command(Consumer<List<Object>> command) {
        this.command = command;
        return this;
    }

    public <S extends CommandSender> CommandBuilder errorHandler(TriConsumer<S, ArgumentResult<?>, String[]> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    public TriConsumer<? extends CommandSender, ArgumentResult<?>, String[]> getErrorHandler() {
        return this.errorHandler;
    }

    public BaseCommand build() {
        return new BaseCommand(
                this.permission,
                this.argument,
                this.children.stream().map(CommandBuilder::build).collect(Collectors.toList()),
                this.command,
                (TriConsumer<CommandSender, ArgumentResult<?>, String[]>) this.errorHandler
        );
    }

}
