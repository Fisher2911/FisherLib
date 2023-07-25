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

package io.github.fisher2911.testplugin;

import io.github.fisher2911.command.anotation.Command;
import io.github.fisher2911.command.anotation.ErrorHandler;
import io.github.fisher2911.command.anotation.SubCommand;
import io.github.fisher2911.command.argument.ArgumentResult;
import org.bukkit.entity.Player;

import java.util.List;

@Command(name = "test", permission = "test")
public class TestCommand {

    @SubCommand()
    public void defaultCommand(Player player) {
        player.sendMessage("In defaultCommand");
    }

    @SubCommand(name = "test1")
    public void test1Command(Player player, List<Integer> numbers, Player lastArg) {
        player.sendMessage("In test1Command: " + numbers + " " + lastArg.getName());
    }

    @SubCommand(name = "test2")
    public void test2Command(Player player, int value, double test) {
        player.sendMessage("In test2Command: " + player + " " + value + " " + test);
    }

    @SubCommand(name = "test3", parent = "test2", errorHandler = "test3ErrorHandler1")
    public void test3Command(Player player, int value, double test, float f, Player other) {
        player.sendMessage("In test3Command: " + player.getName() + " " + value + " " + test + " " + f + " " + other.getName());
    }

    @SubCommand(name = "test3", parent = "test2", errorHandler = "test3ErrorHandler2")
    public void test3Command(Player player, int value, double test, float f, boolean bool) {
        player.sendMessage("In test3Command: " + player + " " + value + " " + test + " " + f + " " + bool);
    }

    @ErrorHandler
    public void errorHandler(Player player, ArgumentResult<?> argumentResult, String[] args) {
        final String error = argumentResult.getError();
        if (error == null) return;
        player.sendMessage("Default error handler: " + error);
    }

    @ErrorHandler("test3ErrorHandler1")
    public void errorHandler2(Player player, ArgumentResult<?> argumentResult, String[] args) {
        final String error = argumentResult.getError();
        if (error == null) return;
        player.sendMessage("Error handler 1: " + error);
    }

    @ErrorHandler("test3ErrorHandler2")
    public void errorHandler3(Player player, ArgumentResult<?> argumentResult, String[] args) {
        final String error = argumentResult.getError();
        if (error == null) return;
        player.sendMessage("Error handler 2: " + error);
    }


}
