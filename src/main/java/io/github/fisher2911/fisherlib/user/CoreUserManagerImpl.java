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

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CoreUserManagerImpl<T extends CoreUser> implements CoreUserManager<T> {

    private final Map<UUID, T> userMap;
    private final Map<String, T> byName;

    public CoreUserManagerImpl(Map<UUID, T> userMap) {
        this.userMap = userMap;
        this.byName = new HashMap<>();
    }

    public Optional<T> get(UUID uuid) {
        return Optional.ofNullable(this.userMap.get(uuid));
    }

    @Nullable
    public T forceGet(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) return null;
        if (sender instanceof final Player player) {
            final UUID uuid = player.getUniqueId();
            return this.forceGet(uuid);
        }
        return null;
    }

    @Nullable
    public T forceGet(UUID uuid) {
        return this.userMap.get(uuid);
    }

    public Optional<T> getUserByName(String name, boolean searchDatabase) {
        final T user = this.byName.get(name);
        return Optional.ofNullable(user);
    }

    public void addUser(T user) {
        this.userMap.put(user.getId(), user);
        this.byName.put(user.getName(), user);
    }

    @Nullable
    public T removeUser(UUID uuid) {
        final T user = this.userMap.remove(uuid);
        if (user == null) return null;
        this.byName.remove(user.getName());
        return user;
    }


}
