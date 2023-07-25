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

import io.github.fisher2911.command.anotation.Command;
import io.github.fisher2911.command.anotation.ErrorHandler;
import io.github.fisher2911.command.anotation.SubCommand;
import io.github.fisher2911.command.argument.Argument;
import io.github.fisher2911.command.error.CommandParameterNotFoundException;
import io.github.fisher2911.command.error.CommandParseException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CommandParser {

    public static FisherLibCommand parseClass(Object commandObject, ArgumentSupplier argumentSupplier) {
        final Class<?> clazz = commandObject.getClass();
        final Command command = clazz.getAnnotation(Command.class);
        if (command == null) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " is not annotated with @Command");
        }
        final Map<String, Method> errorHandlers = getErrorHandlers(clazz.getMethods());
        final CommandBuilder parent = CommandBuilder.create(command.name(), command.permission());
        addErrorHandler(
                commandObject,
                "",
                errorHandlers,
                parent,
                null
        );
        final Map<String, CommandBuilder> commandMap = new HashMap<>();
        // methods that didn't have their parent specified at the time of being processed
        final LinkedList<Method> toProcess = new LinkedList<>(Arrays.asList(clazz.getMethods()));
        while (!toProcess.isEmpty()) {
            final Method method = toProcess.poll();
            addChildMethod(
                    commandObject,
                    parent,
                    method,
                    commandMap,
                    toProcess,
                    argumentSupplier,
                    errorHandlers
            );
        }
        return new FisherLibCommand(parent.build());
    }

    private static void addChildMethod(
            Object commandObject,
            CommandBuilder parent,
            Method method,
            Map<String, CommandBuilder> commandMap,
            LinkedList<Method> toProcess,
            ArgumentSupplier argumentSupplier,
            Map<String, Method> errorHandlers
    ) {
        final SubCommand subCommand = method.getAnnotation(SubCommand.class);
        if (subCommand == null) return;
        final String subName = subCommand.name();
        final @Nullable String subCommandParentName = subCommand.parent().isEmpty() ? null : subCommand.parent();
        final CommandBuilder builder;
        if (subCommandParentName != null) {
            builder = commandMap.get(subCommandParentName);
        } else {
            builder = parent;
        }
        if (builder == null) {
            toProcess.add(method);
            return;
        }
        CommandBuilder child = CommandBuilder.create(subName, subCommand.permission());
        addErrorHandler(
                commandObject,
                subCommand,
                errorHandlers,
                child,
                builder
        );
        final Class<?>[] parameterTypes = method.getParameterTypes();
        CommandBuilder currentBuilder = child;
        final Class<?> senderType = parameterTypes[0];
        int parameterIndex = 0;
        for (final Class<?> parameter : parameterTypes) {
            if (parameterIndex++ == 0) continue;
            final Argument<?> argument;
            if (parameter.isAssignableFrom(List.class)) {
                argument = argumentSupplier.getListArgument(",",
                        ((ParameterizedType) method.getGenericParameterTypes()[parameterIndex - 1]).getActualTypeArguments()[0]);
            } else {
                argument = argumentSupplier.getArgument(parameter);
            }
            if (argument == null) {
                throw new CommandParameterNotFoundException(parameter);
            }
            currentBuilder = currentBuilder.argument(argument)
                    .command(list -> {
                        try {
                            final Object senderObject = list.get(0);
                            if (!senderType.isAssignableFrom(senderObject.getClass())) {
                                list.set(0, null);
                            }
                            method.invoke(commandObject, list.toArray());
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            throw new CommandParseException(commandObject.getClass(), e);
                        }
                    }).errorHandler(currentBuilder.getErrorHandler());
        }
        if (parameterTypes.length == 1) {
            final CommandBuilder noArgBuilder;
            if (subName.isEmpty()) {
                noArgBuilder = parent;
            } else {
                noArgBuilder = currentBuilder;
            }
            noArgBuilder.command(list -> {
                try {
                    final Object senderObject = list.get(0);
                    if (!senderType.isAssignableFrom(senderObject.getClass())) {
                        list.set(0, null);
                    }
                    method.invoke(commandObject, list.toArray());
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new CommandParseException(commandObject.getClass(), e);
                }
            });
        }
        if (!subName.isEmpty()) {
            commandMap.put(subName, child);
            builder.child(c -> c.addChild(child));
        }
    }

    private static void addErrorHandler(
            Object commandObject,
            SubCommand subCommand,
            Map<String, Method> errorHandlers,
            CommandBuilder builder,
            @Nullable CommandBuilder parent
    ) {
        addErrorHandler(
                commandObject,
                subCommand.errorHandler(),
                errorHandlers,
                builder,
                parent
        );
    }

    private static void addErrorHandler(
            Object commandObject,
            String errorHandlerName,
            Map<String, Method> errorHandlers,
            CommandBuilder builder,
            @Nullable CommandBuilder parent
    ) {
        if (errorHandlerName.equals("") && parent != null) {
            builder.errorHandler(parent.getErrorHandler());
            return;
        }
        final Method errorHandler = errorHandlers.get(errorHandlerName);
        if (errorHandler == null) {
            throw new IllegalArgumentException("Error handler " + errorHandlerName + " not found");
        }
        builder.errorHandler((sender, result, args) -> {
            try {
                errorHandler.invoke(commandObject, sender, result, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static Map<String, Method> getErrorHandlers(Method[] methods) {
        final Map<String, Method> errorHandlers = new HashMap<>();
        for (final Method method : methods) {
            final ErrorHandler errorHandler = method.getAnnotation(ErrorHandler.class);
            if (errorHandler == null) continue;
            final Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 3) {
                throw new IllegalArgumentException("Error handler method " + method.getName() + " in class " + method.getDeclaringClass().getName() +
                        " must have exactly three parameters of types: CommandSender/Player, ArgumentResult and String[]");
            }
            errorHandlers.put(errorHandler.value(), method);
        }
        return errorHandlers;
    }

}
