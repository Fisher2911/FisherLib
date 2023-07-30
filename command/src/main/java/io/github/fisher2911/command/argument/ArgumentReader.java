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
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

public class ArgumentReader {

    private final CommandSender sender;
    private final String[] args;
    private int index;

    private ArgumentReader(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public <T> ArgumentResult<T> read(Argument<T> argument) {
        return argument.parse(this);
    }

    public String next() {
        return this.args[this.index++];
    }

    public int getIndex() {
        return this.index;
    }

    public void previous() {
        this.index--;
    }

    public boolean hasNext() {
        return this.index < this.args.length;
    }

    public CommandSender getSender() {
        return this.sender;
    }

    public static ArgumentReader newReader(CommandSender sender, String[] unfilteredArgs) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < unfilteredArgs.length; i++) {
            builder.append(unfilteredArgs[i]);
            if (i != unfilteredArgs.length - 1) {
                builder.append(" ");
            }
        }
        final List<String> list = new ArrayList<>();
        final StringBuilder current = new StringBuilder();
        boolean quote = false;
        for (int index = 0; index < builder.length(); index++) {
            final char c = builder.charAt(index);
            if (c == '"') {
                if (quote) {
                    list.add(current.toString());
                    current.setLength(0);
                    index++;
                }
                quote = !quote;
                continue;
            }
            if (c == ' ') {
                if (!quote) {
                    if (builder.charAt(index - 1) == ' ') {
                        current.append(c);
                        continue;
                    }
                    list.add(current.toString());
                    current.setLength(0);
                    continue;
                }
                if (current.isEmpty()) continue;
            }
            current.append(c);
        }
        if (!current.isEmpty()) {
            list.add(current.toString());
        }
        if (unfilteredArgs[unfilteredArgs.length - 1].equals("")) {
            list.add("");
        }
        return new ArgumentReader(sender, list.toArray(new String[0]));
    }

    @Contract(pure = true)
    public String[] getArgs() {
        final String[] copy = new String[this.args.length];
        System.arraycopy(this.args, 0, copy, 0, this.args.length);
        return copy;
    }

}
