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

package io.github.fisher2911.command;

import io.github.fisher2911.command.command.ArgumentSupplier;
import io.github.fisher2911.command.command.CommandParser;
import io.github.fisher2911.command.command.FisherLibCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class CommandManager {

    private final Map<String, FisherLibCommand> baseCommands;
    private final Field bukkitCommandMap;
    private final CommandMap commandMap;

    public CommandManager(Map<String, FisherLibCommand> baseCommands) {
        this.baseCommands = baseCommands;
        try {
            this.bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            this.bukkitCommandMap.setAccessible(true);
            this.commandMap = (CommandMap) this.bukkitCommandMap.get(Bukkit.getServer());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to register commands");
        }
    }

    public void register(Class<?> clazz, ArgumentSupplier argumentSupplier) {
        try {
            final Object instance = clazz.getDeclaredConstructor().newInstance();
            final FisherLibCommand command = CommandParser.parseClass(instance, argumentSupplier);
            this.baseCommands.put(command.getName(), command);
            this.commandMap.register(command.getName(), command);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException exception) {
            exception.printStackTrace();
            throw new RuntimeException("Failed to register command: " + clazz.getName());
        }
    }

}
