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
