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
