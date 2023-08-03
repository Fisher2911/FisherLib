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
import io.github.fisher2911.command.argument.ArgumentReader;
import io.github.fisher2911.command.argument.ArgumentResult;
import io.github.fisher2911.command.argument.LiteralArgument;
import io.github.fisher2911.common.util.function.TriConsumer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class BaseCommand {

    private final String permission;
    private final Argument<?> argument;
    // todo - change to multimap of Class, BaseCommand
    private final List<BaseCommand> children;
    private final Consumer<List<Object>> command;
    private final @Nullable TriConsumer<CommandSender, ArgumentResult<?>, String[]> errorHandler;

    public BaseCommand(
            String permission,
            Argument<?> argument,
            List<BaseCommand> children,
            Consumer<List<Object>> command,
            @Nullable TriConsumer<CommandSender, ArgumentResult<?>, String[]> errorHandler
    ) {
        this.permission = permission;
        this.argument = argument;
        this.children = children;
        this.command = command;
        this.errorHandler = errorHandler;
        this.children.sort((a, b) -> a.getArgument().getPriority(b.getArgument()));
    }

    public void execute(CommandSender sender, String[] args) {
        if (!this.permission.equals("") && !sender.hasPermission(this.permission)) {
            if (this.errorHandler == null) return;
            this.errorHandler.accept(
                    sender,
                    ArgumentResult.noPermission(this.permission),
                    args
            );
            return;
        }
        final ArgumentReader reader = ArgumentReader.newReader(sender, args);
        final ArgumentResultData resultData = this.execute(reader);
        final BaseCommand command = resultData.command();
        final TriConsumer<CommandSender, ArgumentResult<?>, String[]> errorHandler = command == null ? this.errorHandler : command.errorHandler;
        if (resultData.result().isFailure() || command == null) {
            if (errorHandler == null) return;
            errorHandler.accept(sender, resultData.result(), args);
            return;
        }
        final Consumer<List<Object>> consumer = command.command;
        if (consumer == null) {
            if (errorHandler == null) return;
            errorHandler.accept(sender, resultData.result(), args);
            return;
        }
        final List<Object> parsed = resultData.parsed();
        parsed.add(0, sender);
        try {
            consumer.accept(parsed);
        } catch (Exception e) {
            if (errorHandler == null) {
                return;
            }
            errorHandler.accept(sender, ArgumentResult.exception(e), args);
        }
    }

    @Contract(mutates = "param1")
    private @NotNull ArgumentResultData execute(ArgumentReader reader) {
        final ArgumentResult<?> result = this.argument.parse(reader);
        final Object resultValue = result.getResult();
        if (resultValue == null) {
            return new ArgumentResultData(Collections.emptyList(), null, result);
        }
        if (!reader.hasNext()) {
            return new ArgumentResultData(new ArrayList<>(), this, result);
        }
        return this.executeChildren(reader, new ArgumentResultData(new ArrayList<>(), null, result));
    }

    @Contract(mutates = "param1")
    private @NotNull ArgumentResultData executeChildren(ArgumentReader reader, ArgumentResultData previousResult) {
        if (this.children.isEmpty()) {
            if (!reader.hasNext()) {
                return previousResult;
            }
            final String next = reader.next();
            reader.previous();
            return new ArgumentResultData(previousResult.parsed(), previousResult.command(), ArgumentResult.invalidArgument(next));
        }
        ArgumentResultData furthestError = null;
        for (final BaseCommand child : this.children) {
            final ArgumentResult<?> result = child.argument.parse(reader);
            final Object resultValue = result.getResult();
            final List<Object> parsed = new ArrayList<>(previousResult.parsed());
            if (result.isSuccess()) {
                if (!(child.argument instanceof LiteralArgument)) {
                    parsed.add(resultValue);
                }
                final ArgumentResultData resultData = new ArgumentResultData(parsed, child, result);
                if (!reader.hasNext()) {
                    if (!child.hasPermission(reader.getSender())) {
                        return new ArgumentResultData(parsed, child, ArgumentResult.noPermission(child.permission));
                    }
                    return resultData;
                }
                final ArgumentResultData childResult = child.executeChildren(
                        reader,
                        resultData
                );
                if (childResult.result().isSuccess()) {
                    return childResult;
                }
                furthestError = childResult;
                reader.previous();
            }
        }
        if (furthestError == null) {
            return new ArgumentResultData(
                    previousResult.parsed(),
                    previousResult.command(),
                    previousResult.result()
            );
        }
        return new ArgumentResultData(
                previousResult.parsed(),
                furthestError.command(),
                furthestError.result()
        );
    }

    public boolean hasPermission(CommandSender sender) {
        return this.permission.equals("") || sender.hasPermission(this.permission);
    }

    private final record ArgumentResultData(
            List<Object> parsed,
            BaseCommand command,
            ArgumentResult<?> result
    ) {

    }

    public @NotNull List<String> getTabCompletions(CommandSender sender, String[] args) {
        final String[] argsCopy = args.clone();
        final List<String> tabCompletions = new ArrayList<>();
        final ArgumentReader reader = ArgumentReader.newReader(sender, argsCopy);
        reader.next();
        final List<Object> previousArgs = new ArrayList<>();
        final List<String> childTabCompletions = this.getChildrenTabCompletions(
                sender,
                previousArgs,
                reader
        );
        if (childTabCompletions != null) {
            tabCompletions.addAll(childTabCompletions);
        }
        return tabCompletions;
    }

    private @Nullable List<String> getChildrenTabCompletions(
            CommandSender sender,
            List<Object> previousArgs,
            ArgumentReader reader
    ) {
        if (!reader.hasNext()) return null;
        final List<String> tabCompletions = new ArrayList<>();
        for (final BaseCommand baseCommand : this.children) {
            if (!baseCommand.hasPermission(sender)) continue;
            final String next = reader.next();
            reader.previous();
            final ArgumentResult<?> argumentResult = baseCommand.getArgument().parse(reader);
            if (argumentResult.isFailure()) {
                reader.next();
            }
            if (!reader.hasNext() && argumentResult.isFailure()) {
                final List<String> childTabCompletions = baseCommand.getArgument().getTabCompletions(
                        previousArgs,
                        next,
                        sender
                );
                reader.previous();
                if (childTabCompletions == null) continue;
                tabCompletions.addAll(childTabCompletions);
                continue;
            }
            if (argumentResult.isFailure()) {
                reader.previous();
                continue;
            }
            if (argumentResult.isSuccess() && !reader.hasNext()) {
                reader.previous();
                break;
            }
            final List<Object> newPreviousArgs = new ArrayList<>(previousArgs);
            if (!(baseCommand.getArgument() instanceof LiteralArgument)) {
                newPreviousArgs.add(argumentResult.getResult());
            }
            final List<String> childTabCompletions = baseCommand.getChildrenTabCompletions(
                    sender,
                    newPreviousArgs,
                    reader
            );
            reader.previous();
            if (childTabCompletions == null) continue;
            tabCompletions.addAll(childTabCompletions);
        }
        return tabCompletions;
    }

    public @Nullable String getName() {
        if (!(this.argument instanceof final LiteralArgument literalArgument)) {
            return null;
        }
        return literalArgument.getMatch();
    }

    public String getPermission() {
        return this.permission;
    }

    public Argument<?> getArgument() {
        return this.argument;
    }

    public String getPaths() {
        final StringBuilder output = new StringBuilder();
        for (final BaseCommand child : this.children) {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.getArgument().getType())
                    .append(" (")
                    .append(this.getName())
                    .append(") ")
                    .append("-> ")
                    .append(child.getPaths());
            output.append(builder).append("\n");
        }
        if (this.children.isEmpty()) {
            output.append(this.getArgument().getType())
                    .append(" (")
                    .append(this.getName())
                    .append(")");
        }
        return output.toString();
    }

}
