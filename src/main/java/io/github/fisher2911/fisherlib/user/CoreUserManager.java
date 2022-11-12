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

package io.github.fisher2911.fisherlib.user;

import io.github.fisher2911.fisherlib.manager.Manager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface CoreUserManager<T extends CoreUser> extends Manager<T, UUID> {

    /**
     * Used to get a user from a manager without any loading of data
     * @param sender the sender to get the user from
     * @return the user
     */
    @Nullable
    T forceGet(CommandSender sender);

}
