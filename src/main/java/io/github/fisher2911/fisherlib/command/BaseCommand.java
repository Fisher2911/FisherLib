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

package io.github.fisher2911.fisherlib.command;

import io.github.fisher2911.fisherlib.FishPlugin;
import io.github.fisher2911.fisherlib.command.help.CommandHelp;
import io.github.fisher2911.fisherlib.command.help.CommandHelpUtil;
import io.github.fisher2911.fisherlib.config.BaseSettings;
import io.github.fisher2911.fisherlib.message.Message;
import io.github.fisher2911.fisherlib.message.MessageHandler;
import io.github.fisher2911.fisherlib.user.CoreUser;
import io.github.fisher2911.fisherlib.user.CoreUserManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public abstract class BaseCommand<T extends CoreUser, Z extends FishPlugin<T, Z>, C extends BaseCommand<T, Z, C>> {

    protected final Z plugin;
    protected final CoreUserManager<T> userManager;
    @Nullable
    protected final C parent;
    protected final String name;
    @Nullable
    protected final String dynamicArgs;
    @Nullable
    protected final String permission;
    protected final CommandSenderType senderType;
    protected final int minArgs;
    protected final int maxArgs;
    protected final Map<String, C> subCommands;
    protected final List<CommandHelp> commandHelp;
    protected final BaseSettings settings;
    protected final MessageHandler messageHandler;

    public BaseCommand(
            Z plugin,
            @Nullable C parent,
            String name,
            @Nullable String dynamicArgs,
            @Nullable String permission,
            CommandSenderType senderType,
            int minArgs,
            int maxArgs,
            Map<String, C> subCommands,
            BaseSettings settings
    ) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.parent = parent;
        this.name = name;
        this.dynamicArgs = dynamicArgs;
        this.permission = permission;
        this.senderType = senderType;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
        this.subCommands = subCommands;
        this.commandHelp = new ArrayList<>();
        this.settings = settings;
        this.messageHandler = MessageHandler.getInstance(this.plugin);
    }

    public BaseCommand(
            Z plugin,
            @Nullable C parent,
            String name,
            @Nullable String permission,
            CommandSenderType senderType,
            int minArgs,
            int maxArgs,
            Map<String, C> subCommands,
            BaseSettings settings
    ) {
        this(plugin, parent, name, null, permission, senderType, minArgs, maxArgs, subCommands, settings);
    }

    public void handleArgs(CommandSender sender, String[] args, String[] previousArgs) {
        final T user = this.userManager.forceGet(sender);
        if (user == null) {
            this.messageHandler.sendMessage(sender, Message.USER_DATA_LOAD_ERROR);
            return;
        }
        if (!this.senderType.canExecute(sender)) {
            this.messageHandler.sendMessage(user, Message.INVALID_COMMAND_EXECUTOR);
            return;
        }
        if (this.permission != null && !user.hasPermission(this.permission)) {
            this.messageHandler.sendMessage(user, Message.NO_COMMAND_PERMISSION);
            return;
        }
        final int argsLength = args.length;
        final String[] newPrevious = this.getPreviousArgs(args, previousArgs);
        if ((this.minArgs != -1 && argsLength < this.minArgs) || (this.maxArgs != -1 && argsLength > this.maxArgs)) {
            this.sendHelp(user, 0);
            return;
        }
        if (args.length == 0) {
            this.execute(user, args, previousArgs);
            return;
        }
        final String first = args[0];
        final C subCommand = this.subCommands.get(first.toLowerCase());
        if (subCommand != null) {
            final String[] newArgs = this.getNewArgs(args);
            subCommand.handleArgs(sender, newArgs, newPrevious);
            return;
        }
        this.execute(user, args, newPrevious);
    }

    public abstract void execute(T user, String[] args, String[] previousArgs);

    public void addSubCommand(C command, boolean updateHelp) {
        this.subCommands.put(command.name, command);
        if (updateHelp) {
            this.setHelpCommands();
        }
    }

    public void addSubCommand(C command) {
        this.addSubCommand(command, false);
    }

    @Nullable
    public List<String> getTabs(T user, String[] args, String[] previousArgs, boolean defaultTabIsNull) {
        final List<String> tabs = new ArrayList<>();
        if (this.permission != null && !user.hasPermission(this.permission)) {
            if (defaultTabIsNull) return null;
            return tabs;
        }
        final int argsLength = args.length;
        final String[] newPrevious = this.getPreviousArgs(args, previousArgs);
        if ((this.minArgs != -1 && argsLength < this.minArgs) || (this.maxArgs != -1 && argsLength > this.maxArgs)) {
            if (defaultTabIsNull) return null;
            return tabs;
        }
        if (args.length == 0) {
            if (defaultTabIsNull) return null;
            return tabs;
        }
        final String first = args[0];
        final C subCommand = this.subCommands.get(first.toLowerCase());
        if (subCommand != null) {
            final String[] newArgs = this.getNewArgs(args);
            return subCommand.getTabs(user, newArgs, newPrevious, defaultTabIsNull);
        }
        if (args.length != 1) {
            if (defaultTabIsNull) return null;
            return tabs;
        }
        final String previous = previousArgs.length > 0 ? previousArgs[previousArgs.length - 1] : "";
        for (C command : this.subCommands.values()) {
            if (!command.name.equalsIgnoreCase(previous) && command.name.startsWith(first.toLowerCase())) {
                tabs.add(command.name);
            }
        }
        return tabs;
    }

    private String[] getNewArgs(String[] original) {
        final String[] newArgs = new String[original.length - 1];
        System.arraycopy(original, 1, newArgs, 0, newArgs.length);
        return newArgs;
    }

    private String[] getPreviousArgs(String[] original, String[] previous) {
        final String[] newArgs = new String[previous.length + 1];
        System.arraycopy(previous, 0, newArgs, 0, previous.length);
        if (original.length == 0) return newArgs;
        newArgs[newArgs.length - 1] = original[0];
        return newArgs;
    }

    private void setHelpCommands() {
        this.commandHelp.clear();
        this.commandHelp.addAll(this.getHelp());
        this.commandHelp.sort(Comparator.comparing(CommandHelp::getUsage));
    }

    public void sendHelp(T user, int page) {
        List<CommandHelp> help = this.commandHelp;
        if (this.commandHelp.isEmpty()) {
            help = this.getHelpList();
        }
        CommandHelpUtil.sendCommandHelp(
                this.messageHandler,
                user,
                page,
                help,
                this.settings.getCommandsPerHelpPage(),
                this.settings.getAdminHelpPermission()
        );
    }

    public void sendHelp(T user) {
        this.sendHelp(user, 0);
    }

    protected List<CommandHelp> getHelp() {
        final List<CommandHelp> help = new ArrayList<>();
        for (C command : this.subCommands.values()) {
            help.addAll(command.getHelp());
        }
        if (this.subCommands.isEmpty()) {
            help.addAll(this.getHelpList());
        }
        return help;
    }

    private List<CommandHelp> getHelpList() {
        final List<CommandHelp> help = new ArrayList<>();
        final StringBuilder builder = new StringBuilder();
        C parent = (C) this;
        while (parent != null) {
            if (parent.dynamicArgs != null) {
                help.add(this.getHelp(new StringBuilder(parent.dynamicArgs)));
            }
            parent = parent.parent;
        }
        if (this.dynamicArgs == null) {
            help.add(this.getHelp(builder));
        }
        return help;
    }

    private CommandHelp getHelp(StringBuilder builder) {
        C parent = (C) this;
        while (parent != null) {
            builder.insert(0, parent.name + " ");
            parent = parent.parent;
        }
        return new CommandHelp(this.name, "/" + builder, this.permission);
    }

}
