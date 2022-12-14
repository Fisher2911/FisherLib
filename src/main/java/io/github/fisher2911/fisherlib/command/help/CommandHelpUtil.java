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

package io.github.fisher2911.fisherlib.command.help;

import io.github.fisher2911.fisherlib.message.Message;
import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.user.CoreUser;

import java.util.List;
import java.util.stream.Collectors;

public class CommandHelpUtil {

    private CommandHelpUtil() {
    }

    private static List<CommandHelp> getPageDisplay(
            CoreUser user,
            int page,
            List<CommandHelp> helpList,
            int commandsPerPage
    ) {
        final List<CommandHelp> newList = helpList
                .stream()
                .filter(help -> hasPermission(help, user))
                .collect(Collectors.toList());
        if (newList.isEmpty()) return newList;
        if (page * commandsPerPage > newList.size()) {
            page = newList.size() - commandsPerPage;
        }
        return newList.subList(page * commandsPerPage, Math.min(newList.size(), page * commandsPerPage + commandsPerPage));
    }

    public static void sendCommandHelp(
            MessageHandler messageHandler,
            CoreUser user,
            int page,
            List<CommandHelp> helpList,
            int commandsPerPage,
            String adminHelpPermission
    ) {
        page = Math.max(0, page);
        page = Math.min(page, helpList.size() / commandsPerPage);
        final boolean canSeeAdminInfo = user.hasPermission(adminHelpPermission);
        final CommandInfo info = new CommandInfo(page, page + 2);
        final Message headerMessage = canSeeAdminInfo ? Message.ADMIN_COMMAND_HELP_HEADER : Message.COMMAND_HELP_HEADER;
        messageHandler.sendMessage(
                user,
                headerMessage,
                info
        );
        final Message infoMessage = canSeeAdminInfo ? Message.ADMIN_COMMAND_HELP_FORMAT : Message.COMMAND_HELP_FORMAT;
        getPageDisplay(user, page, helpList, commandsPerPage).forEach(help -> messageHandler.sendMessage(
                user,
                infoMessage,
                help
        ));
        final Message footerMessage = canSeeAdminInfo ? Message.ADMIN_COMMAND_HELP_FOOTER : Message.COMMAND_HELP_FOOTER;
        messageHandler.sendMessage(
                user,
                footerMessage,
                info
        );
    }


    private static boolean hasPermission(CommandHelp help, CoreUser user) {
        final String permission = help.getPermission();
        return permission == null || user.hasPermission(permission);
    }

}
